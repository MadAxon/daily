package ru.vital.daily.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.collection.LongSparseArray;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.disposables.CompositeDisposable;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.MessageAdapter;
import ru.vital.daily.adapter.viewholder.MessageMediaViewHolder;
import ru.vital.daily.adapter.viewholder.MessageViewHolder;
import ru.vital.daily.broadcast.MessageBroadcast;
import ru.vital.daily.databinding.ActivityChatBinding;
import ru.vital.daily.dialog.DeleteMessageDialog;
import ru.vital.daily.fragment.sheet.BaseSheetFragment;
import ru.vital.daily.fragment.sheet.ChatSheetFragment;
import ru.vital.daily.fragment.sheet.SimpleSheetFragment;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.data.Action;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.service.DownloadService;
import ru.vital.daily.service.NotificationService;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.util.SnackbarProvider;
import ru.vital.daily.view.PredictiveLinearLayoutManager;
import ru.vital.daily.view.model.ChatViewModel;

import static ru.vital.daily.enums.Operation.ACTION_MEDIA_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_PROGRESS;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_START;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_SUCCESS;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_END;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_FAILED;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_PROGRESS;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_START;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_SUCCESS;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_FIND;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_FORWARD;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_NOTIFICATION_OPEN;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_READ;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND_FAILED;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND_UPDATED;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_TYPE;
import static ru.vital.daily.enums.Operation.ACTION_PROFILE_ONLINE;

public class ChatActivity extends BaseActivity<ChatViewModel, ActivityChatBinding> implements ActionMode.Callback, BaseSheetFragment.OnDismissListener, MessageMediaClickListener {

    public static final int DRAFT_RESULT = 1;

    public static final String CHAT_ID_EXTRA = "CHAT_ID_EXTRA",
            MEMBER_ID_EXTRA = "MEMBER_ID_EXTRA",
            UNREAD_MESSAGE_COUNT_EXTRA = "UNREAD_MESSAGE_COUNT_EXTRA",
            MESSAGE_ID_EXTRA = "MESSAGE_ID_EXTRA",
            MESSAGE_IDS_EXTRA = "MESSAGE_IDS_EXTRA",
            DRAFT_EXTRA = "DRAFT_EXTRA",
            MESSAGE_ID_NEW_EXTRA = "MESSAGE_ID_NEW_EXTRA",
            MESSAGE_ID_OLD_EXTRA = "MESSAGE_ID_OLD_EXTRA",
            MESSAGE_IDS_NEW_EXTRA = "MESSAGE_IDS_NEW_EXTRA",
            MESSAGE_IDS_OLD_EXTRA = "MESSAGE_IDS_OLD_EXTRA",
            MEDIA_PROGRESS_EXTRA = "MEDIA_PROGRESS_EXTRA",
            MEDIA_ID_EXTRA = "MEDIA_ID_EXTRA",
            MEDIA_ID_NEW_EXTRA = "MEDIA_ID_NEW_EXTRA",
            ACCOUNT_ID_EXTRA = "ACCOUNT_ID_EXTRA",
            DATE_UPDATED_EXTRA = "DATE_UPDATED_EXTRA";

    @Inject
    ClipboardManager clipboardManager;

    @Inject
    DailySocket dailySocket;

    @Inject
    NotificationService notificationService;

    private final int PERMISSION_AUDIO_CODE = 301;

    private ActionMode actionMode;

    private LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver messageReceiver, statusReceiver;

    private Message currentMessageByBottomSheet, currentMessageUploading;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MediaPlayer mediaPlayer = new MediaPlayer();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Media currentVideo;

    private MenuItem menuItemChange, menuItemReply, menuItemDelete;

    @Override
    protected ChatViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.recyclerView.setLayoutManager(linearLayoutManager = new PredictiveLinearLayoutManager(this, RecyclerView.VERTICAL, true));
        viewModel.getMessagesRequest().setPageSize(getIntent().getIntExtra(UNREAD_MESSAGE_COUNT_EXTRA, 100));
        //viewModel.getMessagesRequest().setPageSize(10);

        viewModel.errorEvent.observe(this, throwable -> {
            SnackbarProvider.getSimpleSnackbar(dataBinding.recyclerView, throwable.getMessage());
        });
        viewModel.fabClickedEvent.observe(this, aVoid -> {
            dataBinding.recyclerView.scrollToPosition(0);
        });
        viewModel.sendClickedEvent.observe(this, aVoid -> {
            sendMessage();
        });
        viewModel.attachClickedEvent.observe(this, aVoid -> {
            inputMethodManager.hideSoftInputFromWindow(dataBinding.editText2.getWindowToken(), 0);
            openSheetFragment(new ChatSheetFragment(), viewModel.attachSheetFragmentTag);
        });
        viewModel.isRecording.observe(this, isRecording -> {
            if (isRecording == null) {
                if (viewModel.getSendMessageModel().getAudioFile() != null) {
                    viewModel.stopRecording();
                    final Media media = viewModel.getSendMessageModel().getAudioFile();
                    media.setProgress(0f);
                    media.getFiles().get(0).getMetadata().setDuration(FileUtil.getAudioDuration(this, Uri.parse(media.getFiles().get(0).getUrl())));
                    LongSparseArray<Media> medias = new LongSparseArray<>(1);
                    medias.put(media.getId(), media);
                    viewModel.getSendMessageModel().setMedias(medias);
                    viewModel.getMediaProgressHelper().putMedia(media);
                    sendMessage();
                    stopAudio();
                    dataBinding.imageView22.setVisibility(View.GONE);
                }
            } else if (isRecording) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_AUDIO_CODE);
                else {
                    File file = FileUtil.createTempFile(this, "voice", ".m4a");
                    viewModel.recordAudio(file, getString(R.string.chat_recording));
                    dataBinding.imageView22.setVisibility(View.VISIBLE);
                }
                //Log.i("my_logs", FileUtil.getMimeType(this, Uri.fromFile(file)));
            } else {
                viewModel.stopRecording();
                viewModel.getSendMessageModel().getAudioFile().getFiles().get(0).getMetadata().setDuration(FileUtil.getAudioDuration(this, Uri.parse(viewModel.getSendMessageModel().getAudioFile().getFiles().get(0).getUrl())));
                /*final File wavFile = FileUtil.createTempFile(this, "audio", ".wav");
                viewModel.stopRecording();
                viewModel.parseAudio(file -> {
                    stopAudio();
                    mediaPlayer.setDataSource(viewModel.getSendMessageModel().getAudioFile().getFile().getUrl());
                    mediaPlayer.prepare();
                    viewModel.setCurrentAudio(viewModel.getSendMessageModel().getAudioFile(), false);
                    viewModel.getSendMessageModel().getAudioFile().setDuration(mediaPlayer.getDuration());
                }, wavFile, FileUtil.getMimeType(this, Uri.fromFile(wavFile)));*/
                dataBinding.imageView22.setVisibility(View.GONE);
            }
        });

        viewModel.newMessage.observe(this, message -> {
            executorService.execute(new Process(message, ACTION_MESSAGE_SEND));
            /*dataBinding.getAdapter().newMessage(message);
            if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                dataBinding.recyclerView.scrollToPosition(0);*/
        });
        viewModel.changeMessage.observe(this, message -> {
            currentMessageByBottomSheet.setText(message.getText());
            currentMessageByBottomSheet.setUpdatedAt(new Date());
        });
        viewModel.playPauseBottomTabClickedEvent.observe(this, media -> {
            onClickAudio(media);
        });
        viewModel.audioCancelClickedEvent.observe(this, aVoid -> {
            stopAudio();
        });
        viewModel.playPauseTopTabClickedEvent.observe(this, media -> onClickAudio(media));
        viewModel.playPauseTopTabCloseClickedEvent.observe(this, aVoid -> {
            stopAudio();
        });
        viewModel.changeMediaClickedEvent.observe(this, media -> {
            Intent intent = new Intent(this, MessageBroadcast.class);
            intent.setAction(ACTION_MEDIA_CHANGE);
            intent.putExtra(MessageBroadcast.MEDIA_ID_EXTRA, media.getId());
            intent.putExtra(MessageBroadcast.MEDIA_DESCRIPTION_EXTRA, viewModel.getSendMessageModel().getText());
            sendBroadcast(intent);
            media.setDescription(viewModel.getSendMessageModel().getText());
            viewModel.getSendMessageModel().toDefault();
            if (currentMessageByBottomSheet != null)
                currentMessageByBottomSheet.setUpdatedAt(new Date());
        });
        viewModel.showSnackbarEvent.observe(this, aVoid -> {
            Toast.makeText(this, R.string.chat_message_audio_snackbar, Toast.LENGTH_SHORT).show();
        });
        viewModel.emitReadMessageEvent.observe(this, ids -> {
            dailySocket.emitReadMessage(viewModel.getSendMessageModel().getChatId(), ids);
        });
        viewModel.emitTypeMessageEvent.observe(this, chatId -> {
            dailySocket.emitTypeMessage(chatId);
        });
        viewModel.accountsSendEvent.observe(this, aVoid -> {
            sendAccounts();
        });

        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (shouldIgnoreBroadcastAction(intent))
                    return;
                if (intent.getAction() != null)
                    switch (intent.getAction()) {
                        case ACTION_MESSAGE_SEND_UPDATED:
                            executorService.execute(new Process(new long[]{intent.getLongExtra(MESSAGE_ID_NEW_EXTRA, 0), intent.getLongExtra(MESSAGE_ID_OLD_EXTRA, 0)}, ACTION_MESSAGE_SEND_UPDATED));
                            break;
                        case ACTION_MESSAGE_SEND:
                            viewModel.getMessages(messages -> {
                                executorService.execute(new NewMessagesProcess(messages));
                            }, intent.getLongArrayExtra(MESSAGE_IDS_EXTRA), intent.getLongExtra(CHAT_ID_EXTRA, 0));
                            /*viewModel.getMessage(message -> {
                                        executorService.execute(new Process(message, ACTION_MESSAGE_SEND));
                                    }, intent.getLongExtra(MESSAGE_ID_EXTRA, 0),
                                    intent.getLongExtra(CHAT_ID_EXTRA, 0));*/
                            break;
                        case ACTION_MESSAGE_DELETE:
                            long[] messageIds = intent.getLongArrayExtra(MESSAGE_IDS_EXTRA);
                            executorService.execute(new Process(messageIds, ACTION_MESSAGE_DELETE));
                            viewModel.deleteMessages(messageIds);
                            break;
                        case ACTION_MEDIA_UPLOAD_PROGRESS:
                            if (currentMessageUploading != null && currentMessageUploading.getId() == intent.getLongExtra(MESSAGE_ID_EXTRA, 0)) {
                                Media media = currentMessageUploading.getMedias().get(intent.getLongExtra(MEDIA_ID_EXTRA, 0));
                                if (media != null) {
                                    float progress = intent.getFloatExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, 0);
                                    if (media.getProgress() == null || media.getProgress() != progress)
                                        media.setProgress(progress);
                                }
                            }
                            break;
                        case ACTION_MEDIA_UPLOAD_FAILED:
                            final long failedMessageId = intent.getLongExtra(MESSAGE_ID_EXTRA, 0);
                            if (currentMessageUploading != null && currentMessageUploading.getId() == failedMessageId) {
                                Media media = currentMessageUploading.getMedias().get(intent.getLongExtra(MEDIA_ID_EXTRA, 0));
                                if (media != null)
                                    media.setProgress(null);
                            } else {
                                executorService.execute(new Process(failedMessageId, intent.getLongExtra(MEDIA_ID_EXTRA, 0), ACTION_MEDIA_UPLOAD_FAILED));
                            }
                            break;
                        case ACTION_MEDIA_UPLOAD_SUCCESS:
                            final long messageId = intent.getLongExtra(MESSAGE_ID_EXTRA, 0);
                            final long mediaId = intent.getLongExtra(MEDIA_ID_EXTRA, 0);
                            executorService.execute(new Process(messageId, mediaId, ACTION_MEDIA_UPLOAD_SUCCESS));
                            break;
                        case ACTION_MEDIA_UPLOAD_START:
                            executorService.execute(new Process(intent.getLongExtra(MESSAGE_ID_EXTRA, 0), ACTION_MEDIA_UPLOAD_START));
                            break;
                        case ACTION_MEDIA_UPLOAD_END:
                            currentMessageUploading = null;
                            break;
                        case ACTION_MEDIA_DOWNLOAD_SUCCESS:
                            playVideoOnVisibleViewHolder(false);
                            break;
                        case ACTION_MESSAGE_SEND_FAILED:
                            executorService.execute(new Process(intent.getLongExtra(MESSAGE_ID_EXTRA, 0), ACTION_MESSAGE_SEND_FAILED));
                            break;
                        case ACTION_MESSAGE_TYPE:
                            viewModel.setTyping(true);
                            break;
                        case ACTION_MESSAGE_READ:
                            executorService.execute(new Process(intent.getLongArrayExtra(MESSAGE_IDS_EXTRA), ACTION_MESSAGE_READ));
                            break;
                        case ACTION_MESSAGE_FORWARD:
                            executorService.execute(new ForwardProcess(intent.getLongArrayExtra(MESSAGE_IDS_NEW_EXTRA), intent.getLongArrayExtra(MESSAGE_IDS_OLD_EXTRA)));
                            break;
                        case ACTION_MESSAGE_CHANGE:
                            dataBinding.getAdapter().updateUpdatedAt(intent.getLongExtra(MESSAGE_ID_EXTRA, 0), intent.getLongExtra(DATE_UPDATED_EXTRA, 0));
                            break;
                    }
            }
        };
        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null)
                    switch (intent.getAction()) {
                        case ACTION_PROFILE_ONLINE:

                            break;
                    }
            }
        };

        mediaPlayer.setOnCompletionListener(mp -> {
            viewModel.getCurrentAudio().setPlaying(null);
            if (viewModel.getCurrentAudio().getId() > 0) {
                Message message = dataBinding.getAdapter().nextAudio(viewModel.getCurrentAudio());
                if (message != null) {
                    Media media = message.getMedias().valueAt(0);
                    viewModel.setCurrentAudioMessage(message, false);
                    boolean exist = FileUtil.exists(media.getFiles().get(0).getUrl());
                    if (media.getProgress() == null && !exist && !media.getForceCancelled())
                        startDownload(message, media);
                    else if (media.getProgress() == null && exist)
                        onClickAudio(media);
                } else {
                    viewModel.setCurrentAudio(null, false);
                    viewModel.setCurrentAudioMessage(null, true);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_AUDIO_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File file = FileUtil.createTempFile(this, "voice", ".m4a");
                    viewModel.recordAudio(file, getString(R.string.chat_recording));
                    dataBinding.imageView22.setVisibility(View.VISIBLE);
                } else
                    viewModel.errorEvent.setValue(new Throwable(getString(R.string.permission_audio_denied)));
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("my_logs", "onNewIntent()");
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (getIntent().getStringExtra(DRAFT_EXTRA) != null) {
            try {
                viewModel.setDraft(LoganSquare.parse(getIntent().getStringExtra(DRAFT_EXTRA), Message.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getIntent().removeExtra(DRAFT_EXTRA);

        long toChatId = getIntent().getLongExtra(CHAT_ID_EXTRA, 0);
        notificationService.cancelNotification((int) toChatId);
        if (getIntent().getAction() != null) {
            long fromChatId = viewModel.getSendMessageModel().getChatId();
            switch (getIntent().getAction()) {
                case ACTION_MESSAGE_NOTIFICATION_OPEN:
                    if (toChatId == fromChatId) {
                        viewModel.updateChat(unreadMessageCount -> {
                            updateAdapterLazily(fromChatId, unreadMessageCount + 20);
                        });
                    } else {
                        viewModel.getMessagesRequest().setPageSize(getIntent().getIntExtra(UNREAD_MESSAGE_COUNT_EXTRA, 100));
                        dataBinding.getAdapter().clearAdapter();
                        viewModel.getMessages(messages -> {

                            dataBinding.getAdapter().updateItemsFromDatabase(messages);
                        }, messages -> {
                            final int lastVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                            final int addedListSize = dataBinding.getAdapter().updateItemsFromApi(messages);
                            if (lastVisibleItem == 0 && addedListSize > 0) {
                                dataBinding.recyclerView.scrollToPosition(addedListSize - 1);
                                if (!messages.isEmpty()) dataBinding.fab.show();
                            }
                        }, toChatId, 0);
                    }
                    break;
                case ACTION_MESSAGE_FORWARD:
                    if (dataBinding.getAdapter().getSelectedItems().size() == 0 && currentMessageByBottomSheet != null)
                        dataBinding.getAdapter().getSelectedItems().put(currentMessageByBottomSheet.getId(), currentMessageByBottomSheet);
                    if (toChatId == fromChatId) {
                        forwardMessages(0, fromChatId, new long[dataBinding.getAdapter().getSelectedItems().size()]);
                        viewModel.updateChat(unreadMessageCount -> {
                            updateAdapterLazily(fromChatId, unreadMessageCount + 20);
                        });
                    } else {
                        viewModel.getMessagesRequest().setPageSize(getIntent().getIntExtra(UNREAD_MESSAGE_COUNT_EXTRA, 100));
                        dataBinding.getAdapter().clearAdapter();
                        viewModel.getMessages(messages -> {
                            dataBinding.getAdapter().updateItemsFromDatabase(messages);
                        }, messages -> {
                            final int lastVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                            final int addedListSize = dataBinding.getAdapter().updateItemsFromApi(messages);
                            if (lastVisibleItem == 0 && addedListSize > 0) {
                                dataBinding.recyclerView.scrollToPosition(addedListSize - 1);
                                if (!messages.isEmpty()) dataBinding.fab.show();
                            }

                            forwardMessages(0, fromChatId, new long[dataBinding.getAdapter().getSelectedItems().size()]);

                        }, toChatId, 0);
                    }
                    break;
            }
            getIntent().setAction(null);
        } else {
            if (dataBinding.getAdapter() == null) {
                viewModel.getMessages(messages -> {
                    initAdapter();
                    dataBinding.getAdapter().updateItemsFromDatabase(messages);
                }, messages -> {
                    initAdapter();

                    final int lastVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    final int addedListSize = dataBinding.getAdapter().updateItemsFromApi(messages);
                    if (lastVisibleItem == 0 && addedListSize > 0) {
                        dataBinding.recyclerView.scrollToPosition(addedListSize - 1);
                        if (!messages.isEmpty()) dataBinding.fab.show();
                    }
                }, getIntent().getLongExtra(CHAT_ID_EXTRA, 0), getIntent().getLongExtra(MEMBER_ID_EXTRA, 0));
            } else {
                viewModel.updateChat(unreadMessageCount -> {
                    updateAdapterLazily(viewModel.getSendMessageModel().getChatId(), unreadMessageCount + 20);
                });
            }
        }

        playVideoOnVisibleViewHolder(true);

        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_SEND));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_SEND_UPDATED));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_DELETE));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_SEND_FAILED));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_TYPE));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_READ));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_FORWARD));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE_CHANGE));

        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_UPLOAD_PROGRESS));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_UPLOAD_SUCCESS));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_UPLOAD_FAILED));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_UPLOAD_START));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_UPLOAD_END));

        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_DOWNLOAD_PROGRESS));
        registerReceiver(messageReceiver, new IntentFilter(ACTION_MEDIA_DOWNLOAD_SUCCESS));

        registerReceiver(statusReceiver, new IntentFilter(ACTION_PROFILE_ONLINE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
        unregisterReceiver(statusReceiver);
        compositeDisposable.clear();
        compositeDisposable.add(DisposableProvider.getUICallable(() -> {
            final int startIndex = linearLayoutManager.findFirstVisibleItemPosition();
            final int endIndex = linearLayoutManager.findLastVisibleItemPosition();
            List<MessageMediaViewHolder> viewHolders = new ArrayList<>();
            for (int i = startIndex; i <= endIndex; i++) {
                MessageViewHolder viewHolder = (MessageViewHolder) dataBinding.recyclerView.findViewHolderForLayoutPosition(i);
                if (viewHolder instanceof MessageMediaViewHolder)
                    viewHolders.add((MessageMediaViewHolder) viewHolder);
            }
            return viewHolders;
        }).subscribe(messageMediaViewHolders -> {
            for (MessageMediaViewHolder messageMediaViewHolder : messageMediaViewHolders)
                if (messageMediaViewHolder != null)
                    messageMediaViewHolder.setMediaReleasing();
        }));

        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        if (viewModel.getCurrentAudio() != null && viewModel.getCurrentAudio().getPlaying() != null && viewModel.getCurrentAudio().getPlaying())
            viewModel.getCurrentAudio().setPlaying(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideSoftKeyboard();
        /*if (viewModel.getCurrentAudio().getPlaying() != null && viewModel.getCurrentAudio().getPlaying()) {
            mediaPlayer.pause();
            viewModel.getCurrentAudio().setPlaying(false);
        }*/
        //stopAudio();
        Log.i("my_logs", "ChatActivity onStop");
    }

    @Override
    protected void onDestroy() {
        Log.i("my_logs", "ChatActivity onDestroy");
        super.onDestroy();
        //mediaProgressHelper.clear();
    }

    @Override
    public void finish() {
        Log.i("my_logs", "ChatActivity finish");
        final Draft draft = viewModel.takeDraft();
        if (draft != null) {
            Intent intent = new Intent();
            try {
                intent.putExtra(DRAFT_EXTRA, LoganSquare.serialize(viewModel.takeDraft()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            setResult(DRAFT_RESULT, intent);
        }
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.menu_chat_call:

                return true;*/
            case R.id.menu_chat_more:
                openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_garbage}, new int[]{R.string.chat_remove}), viewModel.settingsChatFragmentTag);
                return true;
            case R.id.menu_chat_notifications:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_message, menu);
        menuItemChange = menu.findItem(R.id.menu_message_change);
        menuItemReply = menu.findItem(R.id.menu_message_reply);
        menuItemDelete = menu.findItem(R.id.menu_message_delete);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_message_copy:
                copyMessagesToClipboard();
                actionMode.finish();
                return true;
            case R.id.menu_message_delete:
                long[] selectedItems = dataBinding.getAdapter().getSelectedItemsIds();
                DeleteMessageDialog dialog = new DeleteMessageDialog(this, R.style.AlertDialog, selectedItems.length, viewModel.getAnotherUser().getUname(), forAll -> {
                    executorService.execute(new Process(selectedItems, ACTION_MESSAGE_DELETE));
                    actionMode.finish();
                    viewModel.deleteMessage(action -> {
                        Intent intent = new Intent(this, MessageBroadcast.class);
                        intent.setAction(ACTION_MESSAGE_DELETE);
                        intent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                        intent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, action.getMessageIds());
                        intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                        intent.putExtra(MessageBroadcast.MESSAGE_FOR_ALL_EXTRA, action.getForAll());
                        sendBroadcast(intent);
                    }, selectedItems, forAll);
                });
                dialog.show();
                return true;
            case R.id.menu_message_resend:
                Intent intent = new Intent(this, ChatSelectorActivity.class);
                intent.putExtra(CHAT_ID_EXTRA, viewModel.getSendMessageModel().getChatId());
                startActivity(intent);
                //actionMode.finish();
                return true;
            case R.id.menu_message_reply:
                viewModel.getSendMessageModel().toDefaultNotIncludingText();
                if (dataBinding.getAdapter().getSelectedMedias().size() == 1) {

                } else
                    viewModel.replyMessage(dataBinding.getAdapter().getSelectedItems().valueAt(0));
                if (!dataBinding.editText2.hasFocus()) {
                    dataBinding.editText2.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                actionMode.finish();
                return true;
            case R.id.menu_message_change:
                viewModel.getSendMessageModel().toDefault();
                if (dataBinding.getAdapter().getSelectedMedias().size() == 1)
                    viewModel.changeMediaDescription(dataBinding.getAdapter().getSelectedMedias(), getString(R.string.chat_message_edit_media_sign));
                else
                    viewModel.changeMessage(dataBinding.getAdapter().getSelectedItems().valueAt(0));
                dataBinding.editText2.requestFocus();
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        dataBinding.getAdapter().cancelSelection();
    }

    private void initAdapter() {
        if (dataBinding.getAdapter() == null) {
            dataBinding.setAdapter(new MessageAdapter(viewModel.getAnotherUser(), this));

            dataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                                dataBinding.fab.hide();
                            } else {
                                dataBinding.fab.show();
                                int size = dataBinding.getAdapter().getItemCount();
                                if (linearLayoutManager.findLastVisibleItemPosition() == size - 1) {
                                    if (viewModel.getMessagesRequest().getPageIndex() * viewModel.getMessagesRequest().getPageSize() + viewModel.getMessagesRequest().getPageSize() <= size)
                                        viewModel.getMoreMessages(messages -> {

                                        }, messages -> {
                                            executorService.execute(new ScrolledUpProcess(messages));
                                        });
                                }
                            }
                            playVideoOnVisibleViewHolder(true);
                            break;
                        default:
                    }
                    Log.i("my_logs", "RecyclerView " + newState);
                }
            });

            dataBinding.getAdapter().clickEvent.observe(this, message -> {
                if (actionMode == null) {
                    Log.i("my_logs", "The message id is " + message.getId());
                    currentMessageByBottomSheet = message;
                    if (message.getSendStatus() == null)
                        openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.string.chat_message_cancel}), viewModel.settingsMessageFragmentTag);
                    else if (message.getSendStatus()) {
                        if (message.getAuthor().getId() == viewModel.getProfile().getId()) {
                            if (System.currentTimeMillis() - message.getCreatedAt().getTime() >= 84000000L && message.getMedias() != null && message.getMedias().size() >= 2)
                                openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_share_outline, R.drawable.ic_arrow_left, R.drawable.ic_copy, R.drawable.ic_garbage}, new int[]{R.string.sheet_message_reply, R.string.sheet_message_forward, R.string.common_copy, R.string.common_delete}), viewModel.settingsMessageFragmentTag);
                            else
                                openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_pencil, R.drawable.ic_share_outline, R.drawable.ic_arrow_left, R.drawable.ic_copy, R.drawable.ic_garbage}, new int[]{R.string.common_change, R.string.sheet_message_reply, R.string.sheet_message_forward, R.string.common_copy, R.string.common_delete}), viewModel.settingsMessageFragmentTag);
                        } else openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_share_outline, R.drawable.ic_arrow_left, R.drawable.ic_copy}, new int[]{R.string.sheet_message_reply, R.string.sheet_message_forward, R.string.common_copy}), viewModel.settingsMessageFragmentTag);
                    } else
                        openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.string.chat_message_retrieve}), viewModel.settingsMessageFragmentTag);
                } else toggleMessage(message);
            });
            dataBinding.getAdapter().longClickEvent.observe(this, message -> {
                if (message.getShouldSync()) return;
                if (actionMode == null)
                    actionMode = startSupportActionMode(this);
                toggleMessage(message);
            });
            dataBinding.getAdapter().videoEvent.observe(this, media -> {
                if (currentVideo != null && currentVideo.getId() != media.getId() && currentVideo.getPlaying() != null && currentVideo.getPlaying())
                    currentVideo.setPlaying(null);
                currentVideo = media;
            });
            dataBinding.getAdapter().messageReadEvent.observe(this, message -> {
                viewModel.readMessage(message);
            });
            dataBinding.getAdapter().contactDailyClickEvent.observe(this, user -> {

            });
            dataBinding.getAdapter().contactClickEvent.observe(this, phone -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            });
            dataBinding.getAdapter().replyClickEvent.observe(this, message -> {
                executorService.execute(new Process(message, ACTION_MESSAGE_FIND));
            });
        }
    }

    private void toggleMessage(Message message) {

        dataBinding.getAdapter().toggleSelectionMessage(message);
        final int selectedItemsSize = dataBinding.getAdapter().getSelectedItems().size();
        final int selectedMediasSize = dataBinding.getAdapter().getSelectedMedias().size();
        if (selectedItemsSize == 0 && selectedMediasSize == 0)
            actionMode.finish();
        if (selectedItemsSize == 0) {
            if (selectedMediasSize <= 1) {
                menuItemReply.setVisible(true);
                if (dataBinding.getAdapter().getSelectedMediasOfAnotherUser() <= 0)
                    menuItemChange.setVisible(true);
                else menuItemChange.setVisible(false);
            } else {
                menuItemChange.setVisible(false);
                menuItemReply.setVisible(false);
            }
        } else if (selectedItemsSize == 1) {
            if (selectedMediasSize == 0 || selectedMediasSize == dataBinding.getAdapter().getSelectedItems().valueAt(0).getMedias().size()) {
                menuItemReply.setVisible(true);
                if (selectedMediasSize <= 1 && dataBinding.getAdapter().getSelectedMediasOfAnotherUser() <= 0 && dataBinding.getAdapter().getSelectedItemsOfAnotherUser() <= 0)
                    menuItemChange.setVisible(true);
                else menuItemChange.setVisible(false);
            } else {
                menuItemChange.setVisible(false);
                menuItemReply.setVisible(false);
            }
        }
        /*if (selectedItemsSize == 1 || selectedItemsSize == 0) {
            if (selectedMediasSize <= 1 && dataBinding.getAdapter().getSelectedItemsOfAnotherUser() <= 0) {
                menuItemReply.setVisible(true);
                menuItemChange.setVisible(true);
            } else if (selectedMediasSize <= 1 && dataBinding.getAdapter().getSelectedMediasOfAnotherUser() <= 0) {
                menuItemReply.setVisible(true);
            } else {
                menuItemChange.setVisible(false);
                menuItemReply.setVisible(false);
            }
        }*/ else {
            menuItemChange.setVisible(false);
            menuItemReply.setVisible(false);
        }

        if (dataBinding.getAdapter().getSelectedItemsOfAnotherUser() > 0)
            menuItemDelete.setVisible(false);
        else menuItemDelete.setVisible(true);
    }

    private Message toggleMedia(Message message, Media media) {
        dataBinding.getAdapter().toggleSelectionMedia(message, media);
        final int selectedItemsSize = dataBinding.getAdapter().getSelectedItems().size();
        final int selectedMediasSize = dataBinding.getAdapter().getSelectedMedias().size();
        if (selectedItemsSize == 0 && selectedMediasSize == 0)
            actionMode.finish();
        if (dataBinding.getAdapter().getSelectedItemsOfAnotherUser() > 0 || dataBinding.getAdapter().getSelectedMediasOfAnotherUser() > 0)
            menuItemDelete.setVisible(false);
        else
            menuItemDelete.setVisible(true);
        if (selectedItemsSize == 0) {
            if (selectedMediasSize <= 1) {
                menuItemReply.setVisible(true);
                if (dataBinding.getAdapter().getSelectedMediasOfAnotherUser() <= 0)
                    menuItemChange.setVisible(true);
                else menuItemChange.setVisible(false);
            } else {
                menuItemChange.setVisible(false);
                menuItemReply.setVisible(false);
            }
            return message;
        } else if (selectedItemsSize == 1) {
            if (selectedMediasSize == 0 || selectedMediasSize == dataBinding.getAdapter().getSelectedItems().valueAt(0).getMedias().size()) {
                menuItemReply.setVisible(true);
                if (selectedMediasSize <= 1 && dataBinding.getAdapter().getSelectedMediasOfAnotherUser() <= 0 && dataBinding.getAdapter().getSelectedItemsOfAnotherUser() <= 0)
                    menuItemChange.setVisible(true);
                else menuItemChange.setVisible(false);
            } else {
                menuItemChange.setVisible(false);
                menuItemReply.setVisible(false);
            }
            return message;
        }
        /*if (selectedItemsSize == 1 || selectedItemsSize == 0) {
            if (selectedMediasSize <= 1 && dataBinding.getAdapter().getSelectedItemsOfAnotherUser() <= 0) {
                menuItemReply.setVisible(true);
                menuItemChange.setVisible(true);
            } else if (selectedMediasSize <= 1 && dataBinding.getAdapter().getSelectedMediasOfAnotherUser() <= 0) {
                menuItemReply.setVisible(true);
            } else {
                menuItemChange.setVisible(false);
                menuItemReply.setVisible(false);
            }
        }*/
        menuItemChange.setVisible(false);
        menuItemReply.setVisible(false);
        return null;
    }

    private void sendMessage() {
        viewModel.
                sendMessage(this::sendActionToRunBackgroundService);
    }

    private void sendAccounts() {
        viewModel.
                sendMessage(action -> {
                    sendActionToRunBackgroundService(action);
                    if (viewModel.getSendMessageModel().getUsers() != null && !viewModel.getSendMessageModel().getUsers().isEmpty())
                        runOnUiThread(() -> sendAccounts());
                });
    }

    private void forwardMessages(int position, long fromChatId, long[] messageIds) {
        viewModel.sendMessage(id -> {
            messageIds[position] = id;
            if (position + 1 == dataBinding.getAdapter().getSelectedItems().size()) {
                viewModel.sendMessage(action -> {
                    if (actionMode != null)
                        actionMode.finish();
                    if (currentMessageByBottomSheet != null)
                        currentMessageByBottomSheet = null;
                    sendActionToRunBackgroundService(action);
                }, messageIds, fromChatId);
            } else
                forwardMessages(position + 1, fromChatId, messageIds);
        }, dataBinding.getAdapter().getSelectedItems().valueAt(position));
    }

    @Override
    public void onDismiss(String tag, int stringId) {
        switch (stringId) {
            case R.string.chat_remove:
                new AlertDialog.Builder(this, R.style.AlertDialog)
                        .setTitle(R.string.chat_remove_title)
                        .setMessage(R.string.chat_remove_summary)
                        .setPositiveButton(R.string.common_delete, (dialog, which) -> {
                            viewModel.removeChat();
                            finish();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(true)
                        .show();
                break;
            case R.string.sheet_message_reply:
                viewModel.getSendMessageModel().toDefaultNotIncludingText();
                viewModel.replyMessage(currentMessageByBottomSheet);
                if (!dataBinding.editText2.hasFocus()) {
                    dataBinding.editText2.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                currentMessageByBottomSheet = null;
                break;
            case R.string.common_change:
                viewModel.getSendMessageModel().toDefaultNotIncludingText();
                if (currentMessageByBottomSheet.getMedias().size() == 1)
                    viewModel.changeMediaDescription(currentMessageByBottomSheet.getMedias(), getString(R.string.chat_message_edit_media_sign));
                else viewModel.changeMessage(currentMessageByBottomSheet);
                if (!dataBinding.editText2.hasFocus()) {
                    dataBinding.editText2.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                //dataBinding.editText2.setSelection(currentMessageByBottomSheet.getText().length());
                break;
            case R.string.common_delete:
                DeleteMessageDialog dialog = new DeleteMessageDialog(this, R.style.AlertDialog, 1, viewModel.getAnotherUser().getUname(), forAll -> {
                    viewModel.deleteMessage(action -> {
                        executorService.execute(new Process(currentMessageByBottomSheet, ACTION_MESSAGE_DELETE));
                        Intent intent = new Intent(this, MessageBroadcast.class);
                        intent.setAction(ACTION_MESSAGE_DELETE);
                        intent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                        intent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, action.getMessageIds());
                        intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                        intent.putExtra(MessageBroadcast.MESSAGE_FOR_ALL_EXTRA, action.getForAll());
                        sendBroadcast(intent);
                    }, currentMessageByBottomSheet, forAll);
                });
                dialog.show();
                break;
            case R.string.sheet_message_forward:
                Intent intent = new Intent(this, ChatSelectorActivity.class);
                intent.putExtra(CHAT_ID_EXTRA, viewModel.getSendMessageModel().getChatId());
                startActivity(intent);
                break;
            case R.string.common_copy:
                copyMessageToClipboard();
                break;
            case R.string.chat_message_cancel:
                cancelSendingMessage(currentMessageByBottomSheet);
                currentMessageByBottomSheet = null;
                break;
            case R.string.chat_message_retrieve:
                viewModel.retrieveSending(this::sendActionToRunBackgroundService, currentMessageByBottomSheet);
                break;
        }
    }

    @Override
    public void onImageOrVideoClick(Message message, Media media) {
        if (actionMode == null) {
            Intent intent = new Intent(this, GalleryActivity.class);
            List<Media> medias = new ArrayList<>();
            final int size = message.getMedias().size();
            for (int i = 0; i < size; i++) {
                Media item = message.getMedias().valueAt(i);
                medias.add(item);
                if (item.getId() == media.getId())
                    intent.putExtra(MediaEditorActivity.MEDIA_CURRENT_EXTRA, i);
            }
            try {
                intent.putExtra(MediaEditorActivity.MEDIA_LIST_EXTRA, LoganSquare.serialize(medias, Media.class));
            } catch (IOException e) {
                e.printStackTrace();
            }

            startActivity(intent);
        } else currentMessageByBottomSheet = toggleMedia(message, media);
    }

    @Override
    public boolean onImageOrVideoLongClick(Message message, Media media) {
        if (!message.getShouldSync()) {
            if (actionMode == null)
                actionMode = startSupportActionMode(this);
            currentMessageByBottomSheet = toggleMedia(message, media);
        }
        return true;
    }

    @Override
    public void onAudioClick(Message message) {
        viewModel.setCurrentAudioMessage(message, true);
        onClickAudio(message.getMedias().valueAt(0));
    }

    @Override
    public void cancelDownload(Message message, Media media) {
        Log.i("my_logs", "cancelDownload");
        //viewModel.cancelDownloadingMedia(media.getId());
        media.setProgress(null);
        media.setHasIconForProgress(true);
        media.setForceCancelled(true);
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(MEDIA_ID_EXTRA, media.getId());
        intent.setAction(ACTION_MEDIA_DOWNLOAD_CANCEL);
        DownloadService.enqueueWork(this, intent);

        viewModel.updateMedias(message, false);
    }

    @Override
    public void cancelUpload(Message message, Media media) {
        if (message.getShouldSync())
            if (message.getMedias().size() > 1) {
                message.getMedias().remove(media.getId());
                executorService.execute(new Process(message, ACTION_MEDIA_UPLOAD_CANCEL));
                Intent intent = new Intent(ChatActivity.this, MessageBroadcast.class);
                intent.setAction(ACTION_MEDIA_UPLOAD_CANCEL);
                intent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, message.getId());
                intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, message.getChatId());
                intent.putExtra(MessageBroadcast.MEDIA_ID_EXTRA, media.getId());
                sendBroadcast(intent);
                viewModel.updateMedias(message, true);
            } else {
                currentMessageUploading = null;
                cancelSendingMessage(message);
            }
        else cancelDownload(message, media);
    }

    @Override
    public void startDownload(Message message, Media media) {
        Log.i("my_logs", "startDownload");
        media.setProgress(0f);
        media.setHasIconForProgress(false);
        if (viewModel.getMediaProgressHelper().contains(media.getId())) {
            viewModel.getMediaProgressHelper().putMedia(media);
            return;
        }
        media.setForceCancelled(false);
        viewModel.getMediaProgressHelper().putMedia(media);

        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction(ACTION_MEDIA_DOWNLOAD_START);
        intent.putExtra(MEDIA_ID_EXTRA, media.getId());
        intent.putExtra(MESSAGE_ID_EXTRA, message.getId());
        intent.putExtra(CHAT_ID_EXTRA, message.getChatId());
        DownloadService.enqueueWork(this, intent);
    }

    @Override
    public void uploadMedia(Message message) {
        viewModel.retrieveSending(action -> sendActionToRunBackgroundService(action), message);
    }

    @Override
    public void continueUploading(Media media) {
        if (viewModel.getMediaProgressHelper().contains(media.getId())) {
            media.setProgress(0f);
            viewModel.getMediaProgressHelper().putMedia(media);
        }
    }

    private void copyMessageToClipboard() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("copied message", currentMessageByBottomSheet.getText()));
    }

    private void copyMessagesToClipboard() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("copied messages", dataBinding.getAdapter().getMessagesOfSelectedItems()));
    }

    private void playAudio(String url) {
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        if (viewModel.getCurrentAudio() != null)
            viewModel.getCurrentAudio().setPlaying(null);
        Log.i("my_logs", "reset");
    }

    private void onClickAudio(Media media) {
        //if (media.getId() == 0L) return;
        if (viewModel.getCurrentAudio() != null && viewModel.getCurrentAudio().getId() == media.getId()) {
            if (viewModel.getCurrentAudio().getPlaying() != null && viewModel.getCurrentAudio().getPlaying()) {
                mediaPlayer.pause();
                viewModel.getCurrentAudio().setPlaying(false);
            } else {
                mediaPlayer.start();
                viewModel.getCurrentAudio().setPlaying(true);
            }
        } else {
            stopAudio();
            viewModel.setCurrentAudio(media, media.getId() != 0);
            viewModel.getCurrentAudio().setPlaying(true);
            playAudio(media.getFiles().get(0).getUrl());
        }
    }

    private void cancelSendingMessage(Message message) {
        executorService.execute(new Process(message, ACTION_MESSAGE_DELETE));
        viewModel.deleteMessage(message);
        Intent intent = new Intent(this, MessageBroadcast.class);
        intent.setAction(ACTION_MESSAGE_CANCEL);
        intent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, message.getId());
        intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, message.getChatId());
        sendBroadcast(intent);
    }

    private void sendActionToRunBackgroundService(Action action) {
        Intent intent = new Intent(this, MessageBroadcast.class);
        switch (action.getAction()) {
            case ACTION_MESSAGE_SEND:
                intent.setAction(ACTION_MESSAGE_SEND);
                intent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                intent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, action.getMessageId());
                intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                sendBroadcast(intent);
                break;
            case ACTION_MESSAGE_CHANGE:
                intent.setAction(ACTION_MESSAGE_CHANGE);
                intent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                intent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, action.getMessageId());
                intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                sendBroadcast(intent);
                break;
            case ACTION_MESSAGE_FORWARD:
                intent.setAction(ACTION_MESSAGE_FORWARD);
                intent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                intent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, action.getMessageIds());
                intent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                intent.putExtra(MessageBroadcast.FROM_CHAT_ID_EXTRA, action.getFromChatId());
                sendBroadcast(intent);
                break;
        }
    }

    private boolean shouldIgnoreBroadcastAction(Intent intent) {
        return viewModel.getSendMessageModel().getChatId() != null && viewModel.getSendMessageModel().getChatId() != intent.getLongExtra(CHAT_ID_EXTRA, 0);
    }

    private void playVideoOnVisibleViewHolder(boolean setPropertyCallback) {
        compositeDisposable.clear();
        compositeDisposable.add(DisposableProvider.getUICallable(() -> {
            final int startIndex = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            final int endIndex = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            //List<MessageMediaViewHolder> viewHolders = new ArrayList<>();
            for (int i = startIndex; i <= endIndex; i++) {
                MessageViewHolder viewHolder = (MessageViewHolder) dataBinding.recyclerView.findViewHolderForLayoutPosition(i);
                MessageMediaViewHolder messageMediaViewHolder;
                if (viewHolder instanceof MessageMediaViewHolder && (messageMediaViewHolder = (MessageMediaViewHolder) viewHolder).isReadyToPlay())
                    return messageMediaViewHolder;
            }
            return false;
        }).subscribe(o -> {
            if (o instanceof MessageMediaViewHolder)
                ((MessageMediaViewHolder) o).setMediaPlaying(setPropertyCallback);
        }));
    }

    /**
     * @param pageSize +20 extra messages for checking whether they were changed
     */
    private void updateAdapterLazily(long chatId, int pageSize) {
        viewModel.getMessages(messages -> {
            executorService.execute(new LazyListProcess(messages));
        }, chatId, pageSize);
    }

    private class Process implements Runnable {

        @Nullable
        private final Message message;

        @Nullable
        private final long[] messageIds;

        private final String operation;

        private final long messageId, mediaId;

        public Process(String operation) {
            this.message = null;
            this.messageIds = null;
            this.messageId = 0;
            this.mediaId = 0;
            this.operation = operation;
        }

        public Process(@Nullable Message message, String operation) {
            this.message = message;
            this.operation = operation;
            this.mediaId = 0;
            messageIds = null;
            messageId = 0;
        }

        public Process(@Nullable long[] messageIds, String operation) {
            this.message = null;
            this.messageIds = messageIds;
            this.operation = operation;
            this.messageId = 0;
            this.mediaId = 0;
        }

        public Process(long messageId, String operation) {
            this.messageId = messageId;
            this.messageIds = null;
            this.message = null;
            this.operation = operation;
            this.mediaId = 0;
        }

        public Process(long messageId, long mediaId, String operation) {
            this.messageId = messageId;
            this.messageIds = null;
            this.message = null;
            this.operation = operation;
            this.mediaId = mediaId;
        }

        @Override
        public void run() {
            switch (operation) {
                case ACTION_MESSAGE_DELETE:
                    Log.i("my_logs", "delete process started");
                    if (message != null) {
                        final int position = dataBinding.getAdapter().messageDeleted(message);
                        if (position != -1)
                            runOnUiThread(() -> {
                                Log.i("my_logs", "delete process notify " + position);
                                dataBinding.getAdapter().notifyItemRemoved(position);
                            });
                    } else if (messageIds != null) {
                        Iterator<Message> iterator = dataBinding.getAdapter().getIterator();
                        Arrays.sort(messageIds);
                        final int idsSize = messageIds.length;
                        int position = 0;
                        for (int i = idsSize - 1; i >= 0; i--) {
                            int deletedPosition = dataBinding.getAdapter().messageDeleted(iterator, messageIds[i], position);
                            if (deletedPosition != -1) {
                                position = deletedPosition;
                                runOnUiThread(() -> {
                                    Log.i("my_logs", "delete process through the socket" + deletedPosition);
                                    dataBinding.getAdapter().notifyItemRemoved(deletedPosition);
                                });
                            }
                        }
                    }
                    break;
                case ACTION_MESSAGE_SEND:
                    Log.i("my_logs", "send process started");
                    final boolean hasHeader = dataBinding.getAdapter().newMessage(message);
                    runOnUiThread(() -> {
                        Log.i("my_logs", "send process notify");
                        if (hasHeader) dataBinding.getAdapter().notifyItemRangeInserted(0, 2);
                        else dataBinding.getAdapter().notifyItemInserted(0);
                        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                            dataBinding.recyclerView.scrollToPosition(0);
                    });
                    break;
                case ACTION_MESSAGE_SEND_UPDATED:
                    dataBinding.getAdapter().messageSent(messageIds[0], messageIds[1]);
                    break;
                case ACTION_MEDIA_UPLOAD_START:
                    currentMessageUploading = dataBinding.getAdapter().findMessage(messageId);
                    if (currentMessageUploading != null) {
                        Log.i("my_logs", "media upload found with id " + currentMessageUploading.getId());
                    } else {

                        Log.i("my_logs", "media upload finding failed");
                    }
                    break;
                case ACTION_MEDIA_UPLOAD_FAILED:
                    if (currentMessageUploading != null) {
                        Message message1 = dataBinding.getAdapter().findMessage(messageId);
                        if (message1 != null) {
                            Media media1 = currentMessageUploading.getMedias().get(mediaId);
                            media1.setProgress(null);
                        }
                    }
                    break;
                case ACTION_MESSAGE_SEND_FAILED:
                    final Message message = dataBinding.getAdapter().findMessage(messageId);
                    if (message != null)
                        message.setSendStatus(false);
                    break;
                case ACTION_MEDIA_UPLOAD_CANCEL:
                    int position = dataBinding.getAdapter().findMessage(this.message);
                    if (position != -1)
                        runOnUiThread(() -> {
                            Log.i("my_logs", "update media process notify " + position);
                            dataBinding.getAdapter().notifyItemChanged(position);
                        });
                    break;
                case ACTION_MESSAGE_READ:
                    dataBinding.getAdapter().readMessages(messageIds);
                    break;
                case ACTION_MESSAGE_FIND:
                    final int foundPosition = dataBinding.getAdapter().findReply(this.message);
                    if (foundPosition != -1) {
                        runOnUiThread(() -> {
                            linearLayoutManager.scrollToPositionWithOffset(foundPosition, dataBinding.recyclerView.getHeight() / 2);
                        });
                    }
                    break;
                case ACTION_MEDIA_UPLOAD_SUCCESS:
                    dataBinding.getAdapter().audioUploaded(messageId, mediaId);
                    break;
            }
        }
    }

    private class ForwardProcess implements Runnable {

        private final long[] oldMessageIds, newMessageIds;

        private ForwardProcess(long[] newMessageIds, long[] oldMessageIds) {
            this.oldMessageIds = oldMessageIds;
            this.newMessageIds = newMessageIds;
        }

        @Override
        public void run() {
            dataBinding.getAdapter().messageSent(newMessageIds, oldMessageIds);
        }
    }

    private class NewMessagesProcess implements Runnable {

        private final List<Message> messages;

        private NewMessagesProcess(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            Log.i("my_logs", "send process started (list)");
            final int count = dataBinding.getAdapter().updateLazily(messages);
            runOnUiThread(() -> {
                Log.i("my_logs", "send process notify (list)");
                if (count > 0) dataBinding.getAdapter().notifyItemRangeInserted(0, count);
                if (count <= 2 && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    dataBinding.recyclerView.scrollToPosition(0);
            });
        }
    }

    private class LazyListProcess implements Runnable {

        private final List<Message> messages;

        private LazyListProcess(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            final int count = dataBinding.getAdapter().updateLazily(messages);
            runOnUiThread(() -> {
                if (count > 0) dataBinding.getAdapter().notifyItemRangeInserted(0, count);
                if (count <= 2 && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    dataBinding.recyclerView.scrollToPosition(0);
            });
        }
    }

    private class ScrolledUpProcess implements Runnable {

        private final List<Message> messages;

        private ScrolledUpProcess(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            final int positionStart = dataBinding.getAdapter().getItemCount();
            final int count = dataBinding.getAdapter().scrolledUp(messages);
            runOnUiThread(() -> {
                dataBinding.getAdapter().notifyItemRangeInserted(positionStart, count);
            });
        }
    }

}
