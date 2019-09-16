package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.activity.ChatCreateActivity;
import ru.vital.daily.adapter.ChatAdapter;
import ru.vital.daily.databinding.FragmentHomeBinding;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.util.SnackbarProvider;
import ru.vital.daily.view.model.ChatSelectorViewModel;
import ru.vital.daily.view.model.HomeViewModel;
import ru.vital.daily.view.model.MainViewModel;

import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_FORWARD;

public class HomeFragment extends BaseFragment<HomeViewModel, FragmentHomeBinding> {

    private final int CHAT_ACTIVITY_REQUEST = 101;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private MenuItem itemAdd;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected HomeViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.setAdapter(new ChatAdapter());
        if (getArguments() != null) {
            viewModel.setToolbarTitle(getString(R.string.chat_forward));
            setupToolbar(dataBinding.toolbar, true);

            ChatSelectorViewModel chatSelectorViewModel = ViewModelProviders.of(getActivity()).get(ChatSelectorViewModel.class);
            chatSelectorViewModel.typingSocketEvent.observe(this, chatId -> {
                dataBinding.getAdapter().setTyping(chatId);
            });
            chatSelectorViewModel.readMessageEvent.observe(this, socketResponse -> {
                dataBinding.getAdapter().setRead(socketResponse.getChatId(), socketResponse.getChatMessageIds());
            });
            chatSelectorViewModel.updateChatEvent.observe(this, chatId -> {
                if (!dataBinding.getAdapter().setUpdating(chatId))
                    viewModel.getChat(chat -> {
                        executorService.execute(new NewChatProcess(chat));
                    }, chatId);
            });
            dataBinding.getAdapter().clickEvent.observe(this, chat -> {
                //startActivity(new Intent(getContext(), GalleryActivity.class));
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.setAction(ACTION_MESSAGE_FORWARD);
                if (chat.getInfo().getLastMessageId() != null && chat.getInfo().getLastMessageId() == 0)
                    try {
                        intent.putExtra(ChatActivity.DRAFT_EXTRA, LoganSquare.serialize(chat.getInfo().getLastMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, chat.getId());
                //intent.putExtra(ChatActivity.UNREAD_MESSAGE_COUNT_EXTRA, chat.getInfo().getUnreadMessagesCount());
                startActivity(intent/*, CHAT_ACTIVITY_REQUEST*/);
            });
        } else {
            setupToolbar(dataBinding.toolbar, false);
            MainViewModel mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
            mainViewModel.isOnline.observe(this, isOnline -> {
                if (isOnline) {
                    getChats();
                    viewModel.setToolbarTitle(null);
                    //SnackbarProvider.getSuccessSnackbar(dataBinding.getRoot(), getString(R.string.connectivity_alive)).show();
                } else
                    viewModel.setToolbarTitle(getString(R.string.common_connecting));
                    //SnackbarProvider.getWarnSnackbar(dataBinding.getRoot(), getString(R.string.connectivity_lost)).show();
            });
            mainViewModel.typingSocketEvent.observe(this, chatId -> {
                dataBinding.getAdapter().setTyping(chatId);
            });
            mainViewModel.readMessageEvent.observe(this, socketResponse -> {
                dataBinding.getAdapter().setRead(socketResponse.getChatId(), socketResponse.getChatMessageIds());
            });
            mainViewModel.updateChatEvent.observe(this, chatId -> {
                if (!dataBinding.getAdapter().setUpdating(chatId))
                    viewModel.getChat(chat -> {
                        executorService.execute(new NewChatProcess(chat));
                    }, chatId);
            });
            dataBinding.getAdapter().clickEvent.observe(this, chat -> {
                //startActivity(new Intent(getContext(), GalleryActivity.class));
                Intent intent = new Intent(getContext(), ChatActivity.class);
                if (chat.getInfo().getLastMessageId() != null && chat.getInfo().getLastMessageId() == 0)
                    try {
                        intent.putExtra(ChatActivity.DRAFT_EXTRA, LoganSquare.serialize(chat.getInfo().getLastMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, chat.getId());
                /*intent.putExtra(ChatActivity.MESSAGE_ID_EXTRA, 114L);
                intent.putExtra(ChatActivity.MESSAGE_INDEX_EXTRA, 38);*/
                //intent.putExtra(ChatActivity.UNREAD_MESSAGE_COUNT_EXTRA, chat.getInfo().getUnreadMessagesCount());
                startActivity(intent/*, CHAT_ACTIVITY_REQUEST*/);
            });
        }

        RecyclerView.ItemAnimator itemAnimator = dataBinding.recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);

        dataBinding.getAdapter().updateChatEvent.observe(this, chatId -> {
            viewModel.getChat(chat -> {
                executorService.execute(new UpdateChatProcess(chat));
            }, chatId);
        });

        viewModel.searchChats.observe(this, chats -> {
            updateAdapterAfterSearch(chats, viewModel.getDrafts());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null)
            switch (requestCode) {
                case CHAT_ACTIVITY_REQUEST:
                    if (resultCode == ChatActivity.DRAFT_RESULT)
                        try {
                            viewModel.setDraft(LoganSquare.parse(data.getStringExtra(ChatActivity.DRAFT_EXTRA), Draft.class));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    break;
            }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        if (getArguments() == null) {
            inflater.inflate(R.menu.menu_home, menu);
            itemAdd = menu.findItem(R.id.menu_add);
            MenuItem menuItem = menu.findItem(R.id.menu_search);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    itemAdd.setVisible(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    itemAdd.setVisible(true);
                    return true;
                }
            });


            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setQueryHint(getString(R.string.common_search));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("my_logs", newText);
                    viewModel.setSearch(newText);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(getContext(), ChatCreateActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getChats();
    }

    private void getChats() {
        viewModel.getDrafts(drafts -> {
            if (viewModel.getChats().size() > 0) {
                viewModel.getChats(chats -> {

                }, chats -> {
                    updateAdapter(chats, drafts);
                });
            } else {
                viewModel.getChats(chats -> {
                    dataBinding.getAdapter().updateItems(chats);
                    executorService.execute(new DraftProcess(drafts, viewModel.getDraft()));
                }, chats -> {
                    updateAdapter(chats, drafts);
                });
            }
        });
    }

    private void updateAdapter(List<Chat> chats, List<Draft> drafts) {
        final int size = chats.size();
        for (int i = 0; i < size; i++)
            executorService.execute(new ReupdateProcess(chats.get(i), i));
        executorService.execute(new DraftProcess(drafts, viewModel.getDraft()));
    }

    private void updateAdapterAfterSearch(List<Chat> chats, List<Draft> drafts) {
        dataBinding.getAdapter().clearAdapter();
        dataBinding.getAdapter().updateItems(chats);
        executorService.execute(new DraftProcess(drafts, viewModel.getDraft()));
    }

    private class ReupdateProcess implements Runnable {

        private final Chat chat;

        private final int index;

        private ReupdateProcess(Chat chat, int index) {
            this.chat = chat;
            this.index = index;
        }

        @Override
        public void run() {
            final int position = dataBinding.getAdapter().reupdateItems(index, chat);
            if (getActivity() != null) {
                if (position == -2)
                    getActivity().runOnUiThread(() -> {
                        viewModel.saveChat(chat);
                        dataBinding.getAdapter().notifyItemInserted(dataBinding.getAdapter().getItems().size());
                    });
                else if (position >= 0)
                    getActivity().runOnUiThread(() -> {
                        viewModel.updateChat(chat);
                        dataBinding.getAdapter().notifyItemChanged(position);
                    });
            }
        }
    }

    private class DraftProcess implements Runnable {

        private final List<Draft> drafts;

        private final Draft freshDraft;

        private DraftProcess(List<Draft> drafts, Draft freshDraft) {
            this.drafts = drafts;
            this.freshDraft = freshDraft;
        }

        @Override
        public void run() {
            if (freshDraft != null)
                for (Draft draft : drafts)
                    if (draft.getId() == freshDraft.getId()) {
                        draft.setMessage(freshDraft.getMessage());
                        break;
                    }
            dataBinding.getAdapter().draftList(drafts);
        }
    }

    private class NewChatProcess implements Runnable {

        private final Chat chat;

        private NewChatProcess(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void run() {
            final int position = dataBinding.getAdapter().insertNewChat(chat);
            if (getActivity() != null)
                getActivity().runOnUiThread(() -> {
                    switch (position) {
                        case -1:
                            dataBinding.getAdapter().notifyItemInserted(0);
                            break;
                        case 0:
                            dataBinding.getAdapter().notifyItemChanged(0);
                            break;
                        default:
                            dataBinding.getAdapter().notifyItemRangeChanged(0, position + 1);
                    }

                });
        }
    }

    private class UpdateChatProcess implements Runnable {

        private final Chat chat;

        private UpdateChatProcess(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void run() {
            int position = dataBinding.getAdapter().updateChat(chat);
            if (position != -1 && getActivity() != null) {
                Log.i("my_logs", "UpdateChatProcess position " + position);
                getActivity().runOnUiThread(() -> dataBinding.getAdapter().notifyItemRangeChanged(0, position + 1));
            }
        }
    }
}
