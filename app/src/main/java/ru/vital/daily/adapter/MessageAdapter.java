package ru.vital.daily.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.MessageAudioViewHolder;
import ru.vital.daily.adapter.viewholder.MessageContactDailyViewHolder;
import ru.vital.daily.adapter.viewholder.MessageContactViewHolder;
import ru.vital.daily.adapter.viewholder.MessageMediaViewHolder;
import ru.vital.daily.adapter.viewholder.MessageViewHolder;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.enums.MessageContentType;
import ru.vital.daily.enums.MessageType;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.view.model.item.MessageItemViewModel;

public class MessageAdapter extends BaseAdapter<Message, MessageViewHolder, ViewDataBinding> {

    public final SingleLiveEvent<Message> longClickEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Media> videoEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Message> messageReadEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<User> contactDailyClickEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<String> contactClickEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Message> replyClickEvent = new SingleLiveEvent<>();

    private final LongSparseArray<Message> selectedItems = new LongSparseArray<>();

    private final LongSparseArray<Message> audioItems = new LongSparseArray<>();

    private final LongSparseArray<Media> selectedMedias = new LongSparseArray<>();

    private final MessageMediaClickListener mediaClickListener;

    private final LinkedList<Message> items = new LinkedList<>();

    private final User anotherUser;

    private final int START_SIMPLE_VIEW_TYPE = 0,
            END_SIMPLE_VIEW_TYPE = 1,
            START_MEDIA_VIEW_TYPE = 2,
            END_MEDIA_VIEW_TYPE = 3,
            START_MAP_VIEW_TYPE = 4,
            END_MAP_VIEW_TYPE = 5,
            START_LINK_VIEW_TYPE = 6,
            END_LINK_VIEW_TYPE = 7,
            START_CONTACT_VIEW_TYPE = 8,
            END_CONTACT_VIEW_TYPE = 9,
            START_AUDIO_VIEW_TYPE = 10,
            END_AUDIO_VIEW_TYPE = 11,
            START_CONTACT_DAILY_VIEW_TYPE = 12,
            END_CONTACT_DAILY_VIEW_TYPE = 13,
            DATE_VIEW_TYPE = 14,
            UNREAD_VIEW_TYPE = 15;

    private final Calendar currentCalendar = Calendar.getInstance(),
            nextCalendar = Calendar.getInstance();

    private int stableId = -2;

    public MessageAdapter(User anotherUser, MessageMediaClickListener mediaClickListener) {
        this.anotherUser = anotherUser;
        this.mediaClickListener = mediaClickListener;
        currentCalendar.setTimeInMillis(0);
        setHasStableIds(true);
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case START_SIMPLE_VIEW_TYPE:
                return R.layout.item_message_start_simple;
            case END_SIMPLE_VIEW_TYPE:
                return R.layout.item_message_end_simple;
            case START_MEDIA_VIEW_TYPE:
                return R.layout.item_message_start_media;
            case END_MEDIA_VIEW_TYPE:
                return R.layout.item_message_end_media;
            case START_MAP_VIEW_TYPE:
                return R.layout.item_message_start_map;
            case END_MAP_VIEW_TYPE:
                return R.layout.item_message_end_media;
            case START_LINK_VIEW_TYPE:
                return R.layout.item_message_start_link;
            case END_LINK_VIEW_TYPE:
                return R.layout.item_message_end_link;
            case START_CONTACT_VIEW_TYPE:
                return R.layout.item_message_start_contact;
            case END_CONTACT_VIEW_TYPE:
                return R.layout.item_message_end_contact;
            case START_AUDIO_VIEW_TYPE:
                return R.layout.item_message_start_audio;
            case END_AUDIO_VIEW_TYPE:
                return R.layout.item_message_end_audio;
            case START_CONTACT_DAILY_VIEW_TYPE:
                return R.layout.item_message_start_contact_daily;
            case END_CONTACT_DAILY_VIEW_TYPE:
                return R.layout.item_message_end_contact_daily;
            case UNREAD_VIEW_TYPE:
                return R.layout.item_message_unread_header;
            case DATE_VIEW_TYPE:
            default:
                return R.layout.item_message_date_header;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        return null;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(viewType)
                , parent, false);
        switch (viewType) {
            case DATE_VIEW_TYPE:
            case UNREAD_VIEW_TYPE:
                return new MessageViewHolder<>(viewDataBinding);
            case START_AUDIO_VIEW_TYPE:
                MessageAudioViewHolder viewHolder = new MessageAudioViewHolder(viewDataBinding);
                viewHolder.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder.viewModel.setMediaClickListener(mediaClickListener);
                viewHolder.viewModel.setClickEvent(clickEvent);
                viewHolder.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder;
            case END_AUDIO_VIEW_TYPE:
                MessageAudioViewHolder viewHolder2 = new MessageAudioViewHolder(viewDataBinding);
                viewHolder2.viewModel.setAnotherUser(anotherUser);
                viewHolder2.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder2.viewModel.setMediaClickListener(mediaClickListener);
                viewHolder2.viewModel.setClickEvent(clickEvent);
                viewHolder2.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder2;
            case START_MEDIA_VIEW_TYPE:
                MessageMediaViewHolder viewHolder1 = new MessageMediaViewHolder(viewDataBinding);
                viewHolder1.setVideoEvent(videoEvent);
                viewHolder1.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder1.viewModel.setMediaClickListener(mediaClickListener);
                viewHolder1.viewModel.setClickEvent(clickEvent);
                viewHolder1.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder1;
            case END_MEDIA_VIEW_TYPE:
                MessageMediaViewHolder viewHolder3 = new MessageMediaViewHolder(viewDataBinding);
                viewHolder3.viewModel.setAnotherUser(anotherUser);
                viewHolder3.setVideoEvent(videoEvent);
                viewHolder3.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder3.viewModel.setMediaClickListener(mediaClickListener);
                viewHolder3.viewModel.setClickEvent(clickEvent);
                viewHolder3.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder3;
            case START_CONTACT_DAILY_VIEW_TYPE:
                MessageContactDailyViewHolder viewHolder6 = new MessageContactDailyViewHolder(viewDataBinding);
                viewHolder6.viewModel.setUserClickedEvent(contactDailyClickEvent);
                viewHolder6.viewModel.setClickEvent(clickEvent);
                viewHolder6.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder6.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder6;
            case END_CONTACT_DAILY_VIEW_TYPE:
                MessageContactDailyViewHolder viewHolder7 = new MessageContactDailyViewHolder(viewDataBinding);
                viewHolder7.viewModel.setAnotherUser(anotherUser);
                viewHolder7.viewModel.setUserClickedEvent(contactDailyClickEvent);
                viewHolder7.viewModel.setClickEvent(clickEvent);
                viewHolder7.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder7.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder7;
            case START_CONTACT_VIEW_TYPE:
                MessageContactViewHolder viewHolder8 = new MessageContactViewHolder(viewDataBinding);
                viewHolder8.viewModel.setContactClickEvent(contactClickEvent);
                viewHolder8.viewModel.setClickEvent(clickEvent);
                viewHolder8.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder8.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder8;
            case END_CONTACT_VIEW_TYPE:
                MessageContactViewHolder viewHolder9 = new MessageContactViewHolder(viewDataBinding);
                viewHolder9.viewModel.setAnotherUser(anotherUser);
                viewHolder9.viewModel.setContactClickEvent(contactClickEvent);
                viewHolder9.viewModel.setClickEvent(clickEvent);
                viewHolder9.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder9.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder9;
            case END_SIMPLE_VIEW_TYPE:
                MessageViewHolder<MessageItemViewModel> viewHolder4 = new MessageViewHolder<>(viewDataBinding);
                viewHolder4.viewModel.setAnotherUser(anotherUser);
                viewHolder4.viewModel.setClickEvent(clickEvent);
                viewHolder4.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder4.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder4;
            default:
                MessageViewHolder<MessageItemViewModel> viewHolder5 = new MessageViewHolder<>(viewDataBinding);
                viewHolder5.viewModel.setClickEvent(clickEvent);
                viewHolder5.viewModel.setItemLongClickedEvent(longClickEvent);
                viewHolder5.viewModel.setReplyClicked(replyClickEvent);
                return viewHolder5;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message item = items.get(position);
        holder.bind(item);
        messageReadEvent.setValue(item);
    }

    public void toggleSelectionMessage(Message message) {
        if (selectedItems.get(message.getId()) != null) {
            selectedItems.remove(message.getId());
            message.setSelected(false);
            if (message.getMedias() != null && !message.getMedias().isEmpty()) {
                final int size = message.getMedias().size();
                for (int i = 0; i < size; i++)
                    toggleSelectionMedia(message.getMedias().valueAt(i), false);
            }
        } else {
            selectedItems.put(message.getId(), message);
            message.setSelected(true);
            if (message.getMedias() != null && !message.getMedias().isEmpty()) {
                final int size = message.getMedias().size();
                for (int i = 0; i < size; i++)
                    toggleSelectionMedia(message.getMedias().valueAt(i), true);
            }
        }
    }

    public void toggleSelectionMessage(Message message, boolean selected) {
        if (selected)
            selectedItems.put(message.getId(), message);
        else selectedItems.remove(message.getId());
        message.setSelected(selected);
    }

    public void toggleSelectionMedia(Message message, Media media) {
        if (selectedMedias.get(media.getId()) != null) {
            selectedMedias.remove(media.getId());
            media.setSelected(false);
            if (message.getSelected())
                toggleSelectionMessage(message, false);
        } else {
            selectedMedias.put(media.getId(), media);
            media.setSelected(true);
            final int size = message.getMedias().size();
            for (int i = 0; i < size; i++)
                if (!message.getMedias().valueAt(i).getSelected())
                    return;
            toggleSelectionMessage(message, true);
        }
    }

    public void toggleSelectionMedia(Media media, boolean selected) {
        if (selected)
            selectedMedias.put(media.getId(), media);
        else selectedMedias.remove(media.getId());
        media.setSelected(selected);
    }

    public void cancelSelection() {
        final int selectedItemsSize = selectedItems.size();
        for (int i = selectedItemsSize - 1; i >= 0; i--) {
            selectedItems.valueAt(i).setSelected(false);
            selectedItems.removeAt(i);
        }
        final int selectedMediasSize = selectedMedias.size();
        for (int i = selectedMediasSize - 1; i >= 0; i--) {
            selectedMedias.valueAt(i).setSelected(false);
            selectedMedias.removeAt(i);
        }
    }

    public long[] getSelectedItemsIds() {
        final int selectedItemsSize = selectedItems.size();
        long[] ids = new long[selectedItemsSize];
        for (int i = 0; i < selectedItemsSize; i++)
            ids[i] = selectedItems.keyAt(i);
        return ids;
    }

    public long[] getSelectedItemsMedias() {
        final int selectedMediasSize = selectedMedias.size();
        long[] ids = new long[selectedMediasSize];
        for (int i = 0; i < selectedMediasSize; i++)
            ids[i] = selectedMedias.keyAt(i);
        return ids;
    }

    public LongSparseArray<Media> getSelectedMedias() {
        return selectedMedias;
    }

    public LongSparseArray<Message> getSelectedItems() {
        return selectedItems;
    }

    public String getMessagesOfSelectedItems() {
        StringBuilder stringBuilder = new StringBuilder();
        final int selectedItemsSize = selectedItems.size();
        for (int i = 0; i < selectedItemsSize - 1; i++) {
            stringBuilder.append(selectedItems.valueAt(i).getText());
            stringBuilder.append("\n\n");
        }
        stringBuilder.append(selectedItems.valueAt(selectedItemsSize - 1).getText());
        return stringBuilder.toString();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = items.get(position);
        if (message == null) return UNREAD_VIEW_TYPE;
        if (MessageType.message.name().equals(message.getType())) {
            if (message.getMedias() != null && message.getMedias().size() > 0) {
                if (FileType.image.name().equals(message.getMedias().valueAt(0).getType()) || FileType.video.name().equals(message.getMedias().valueAt(0).getType())) {
                    if (message.getAuthor().getId() != anotherUser.getId())
                        return END_MEDIA_VIEW_TYPE;
                    else return START_MEDIA_VIEW_TYPE;
                } else if (FileType.voice.name().equals(message.getMedias().valueAt(0).getType())) {
                    if (message.getAuthor().getId() != anotherUser.getId())
                        return END_AUDIO_VIEW_TYPE;
                    else return START_AUDIO_VIEW_TYPE;
                }
            } else if (message.getAccount() != null) {
                if (message.getAuthor().getId() != anotherUser.getId())
                    return END_CONTACT_DAILY_VIEW_TYPE;
                else return START_CONTACT_DAILY_VIEW_TYPE;
            } else if (MessageContentType.contact.name().equals(message.getContentType())) {
                if (message.getAuthor().getId() != anotherUser.getId())
                    return END_CONTACT_VIEW_TYPE;
                else return START_CONTACT_VIEW_TYPE;
            } else {
                if (message.getAuthor().getId() != anotherUser.getId())
                    return END_SIMPLE_VIEW_TYPE;
                else return START_SIMPLE_VIEW_TYPE;
            }
        } else if (message.getHeaderDate() != null) return DATE_VIEW_TYPE;
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        Message message = items.get(position);
        if (message != null) {
            if (message.getId() != 0 && !message.getShouldSync()) {
                return message.getId();
            }
            return stableId--;
        }
        return stableId--;
    }

    public Iterator<Message> getIterator() {
        return items.iterator();
    }

    public void updateItemsFromDatabase(List<Message> items) {
        if (items.size() != 0) {
            this.items.addAll(0, getListWithDateHeaders(items));
            notifyItemRangeInserted(0, this.items.size());
        }
    }

    public int updateItemsFromApi(List<Message> items) {
        if (items.size() != 0) {
            this.items.addFirst(null);

            LinkedList<Message> messageListWithDateHeaders = getListWithDateHeaders(items);

            this.items.addAll(0, messageListWithDateHeaders);
            notifyItemRangeInserted(0, messageListWithDateHeaders.size() + 1); // + 1 - for UNREAD_VIEW_TYPE item
            return messageListWithDateHeaders.size();
        }
        return 0;
    }

    /**
     * @param message The new message
     * @return true - A date header has been added, false otherwise
     */
    public boolean newMessage(Message message) {
        nextCalendar.setTime(message.getCreatedAt());
        if (currentCalendar.get(Calendar.YEAR) != nextCalendar.get(Calendar.YEAR)
                || currentCalendar.get(Calendar.DAY_OF_YEAR) != nextCalendar.get(Calendar.DAY_OF_YEAR)) {
            items.addFirst(new Message(nextCalendar.getTime()));
            currentCalendar.setTime(nextCalendar.getTime());
            items.addFirst(message);
            if (message.getMedias() != null && message.getMedias().size() > 0) {
                Media media = message.getMedias().valueAt(0);
                if (FileType.voice.name().equals(media.getType()))
                    audioItems.put(media.getId(), message);
            }
            return true;
        }
        items.addFirst(message);
        return false;
    }

    public boolean messageSent(long newId, long oldId) {
        if (newId == 0 || oldId == 0) return false;
        for (Message message : items) {
            if (message.getId() < oldId) return false;
            else if (message.getId() == oldId && message.getShouldSync()) {
                message.setShouldSync(false);
                message.setId(newId);
                message.setSendStatus(true);
                return true;
            }
        }
        return false;
    }

    public int messageDeleted(Message message) {
        /*final int position = items.indexOf(message);
        if (position == -1) return position;
        items.remove(message);
        return position;*/
        int position = -1;
        Iterator<Message> iterator = items.iterator();
        Message nextMessage;
        while (iterator.hasNext()) {
            position++;
            nextMessage = iterator.next();
            if (nextMessage != null) {
                if (message.getId() == nextMessage.getId() && message.getShouldSync() == nextMessage.getShouldSync()) {
                    iterator.remove();
                    return position;
                }
                if (nextMessage.getId() != 0 && message.getId() > nextMessage.getId())
                    return -1;
            }
        }
        return -1;
    }

    public int[] messagesDeleted() {
        int[] positions = new int[selectedItems.size()];
        for (int i = 0; i < selectedItems.size(); i++) {
            positions[i] = items.indexOf(selectedItems.valueAt(i));
            items.remove(selectedItems.valueAt(i));
        }
        return positions;
    }

    public int messageDeleted(Iterator<Message> iterator, long id, int position) {
        Message nextMessage;
        while (iterator.hasNext()) {
            nextMessage = iterator.next();
            if (nextMessage != null) {
                if (id == nextMessage.getId() && !nextMessage.getShouldSync()) {
                    iterator.remove();
                    return position;
                }
                if (nextMessage.getId() != 0 && id > nextMessage.getId())
                    return -1;
                position++;
            }
        }
        return -1;
    }

    public Message findMessage(long id) {
        if (id == 0) return null;
        for (Message message : items)
            if (message != null) {
                if (id == message.getId() && message.getShouldSync())
                    return message;
                else if (message.getId() != 0 && id > message.getId())
                    return null;
            }
        return null;
    }

    public int findReply(Message reply) {
        Message nextMessage;
        int position = 0;
        Iterator<Message> iterator = items.iterator();
        while (iterator.hasNext()) {
            nextMessage = iterator.next();
            if (nextMessage != null) {
                if (reply.getId() == nextMessage.getId() && !nextMessage.getShouldSync()) {
                    nextMessage.setReplyAnimationEvent(true);
                    return position;
                }
                if (nextMessage.getId() != 0 && reply.getId() > nextMessage.getId())
                    return -1;
                position++;
            }
        }
        return -1;
    }

    public void readMessages(long[] ids) {
        Arrays.sort(ids);
        int i;
        final int idsSize = ids.length;
        for (int j = idsSize - 1; j >= 0; j--) {
            for (i = 0; i < items.size(); i++) {
                long id = ids[j];
                Message message = items.get(i);
                if (message != null) {
                    if (id == message.getId() && !message.getShouldSync()) {
                        message.getInfo().setReadAt(new Date());
                        break;
                    } else if (message.getId() != 0 && !message.getShouldSync() && id > message.getId())
                        break;
                }
            }
        }
    }

    public int findMessage(Message message) {
        return items.indexOf(message);
    }

    public void audioUploaded(long mediaId, Message media) {
        audioItems.put(mediaId, media);
    }

    public Message nextAudio(Media media) {
        final int index = audioItems.indexOfKey(media.getId()) + 1;
        if (index < audioItems.size())
            return audioItems.valueAt(index);
        else return null;
    }

    private LinkedList<Message> getListWithDateHeaders(List<Message> messageList) {
        LinkedList<Message> messageListWithDateHeaders = new LinkedList<>();
        for (int i = messageList.size() - 1; i >= 0; i--) {
            final Message message = messageList.get(i);
            if (message.getMedias() != null && message.getMedias().size() > 0) {
                Media media = message.getMedias().valueAt(0);
                if (FileType.voice.name().equals(media.getType()) && media.getId() != 0)
                    audioItems.put(media.getId(), message);
            }
            nextCalendar.setTime(message.getCreatedAt());
            if (currentCalendar.get(Calendar.YEAR) != nextCalendar.get(Calendar.YEAR)
                    || currentCalendar.get(Calendar.DAY_OF_YEAR) != nextCalendar.get(Calendar.DAY_OF_YEAR)) {
                messageListWithDateHeaders.addFirst(new Message(nextCalendar.getTime()));
                currentCalendar.setTime(nextCalendar.getTime());
            }
            messageListWithDateHeaders.addFirst(message);
        }
        return messageListWithDateHeaders;
    }
}
