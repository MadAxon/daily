package ru.vital.daily.adapter;

import android.util.Log;

import java.util.List;

import androidx.collection.LongSparseArray;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.ChatViewHolder;
import ru.vital.daily.databinding.ItemChatBinding;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.repository.data.Message;

public class ChatAdapter extends BaseAdapter<Chat, ChatViewHolder, ItemChatBinding> {

    public final SingleLiveEvent<Long> updateChatEvent = new SingleLiveEvent<>();

    private final LongSparseArray<Chat> itemsSparseArray = new LongSparseArray<>();

    public ChatAdapter() {
        setHasStableIds(true);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_chat;
    }

    @Override
    public ChatViewHolder onCreateViewHolderBinding(ItemChatBinding viewDataBinding, int viewType) {
        ChatViewHolder viewHolder = new ChatViewHolder(viewDataBinding);
        viewHolder.viewModel.setUpdateChatEvent(updateChatEvent);
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    public int reupdateItems(int index, Chat incomingChat) {
        if (this.items.size() > index) {
            Chat currentChatOfIndex = this.items.get(index);
            if (currentChatOfIndex.getId() != incomingChat.getId()
                    || currentChatOfIndex.getInfo().getUnreadMessagesCount() != incomingChat.getInfo().getUnreadMessagesCount()
                    || (currentChatOfIndex.getInfo().getLastMessageId() == null && incomingChat.getInfo().getLastMessageId() != null)
                    || (currentChatOfIndex.getInfo().getLastMessageId() != null && incomingChat.getInfo().getLastMessageId() == null)
                    || (currentChatOfIndex.getInfo().getLastMessageId() != null && incomingChat.getInfo().getLastMessageId() != null && !currentChatOfIndex.getInfo().getLastMessageId().equals(incomingChat.getInfo().getLastMessageId()))) {
                this.items.set(index, incomingChat);
                itemsSparseArray.put(incomingChat.getId(), incomingChat);
                return index;
            }
            return -1;
        }
        this.items.add(incomingChat);
        itemsSparseArray.put(incomingChat.getId(), incomingChat);
        return -2;
    }

    public void draftList(List<Draft> drafts) {
        if (drafts != null)
            for (final Draft draft : drafts) {
                //Chat chat = itemsSparseArray.get(draft.getId());
                for (final Chat chat: items) {
                    if (chat != null && chat.getId() == draft.getId() && draft.getMessage() != null) {
                        chat.getInfo().setLastMessageId(draft.getMessage().getId());
                        chat.getInfo().setLastMessage(draft.getMessage());
                        Log.i("my_logs", "draft was set in adapter " + draft.getMessage().getText());
                        break;
                    }
                }
            }
    }

    @Override
    public void updateItems(List<Chat> items) {
        super.updateItems(items);
        for (Chat chat: items)
            itemsSparseArray.put(chat.getId(), chat);
    }

    public int insertNewChat(Chat chat) {
        itemsSparseArray.put(chat.getId(), chat);
        if (itemsSparseArray.get(chat.getId()) == null) {
            items.add(0, chat);
            return -1;
        } else {
            int position = items.indexOf(chat);
            for (int i = position; i >= 1; i--) {
                items.set(i, items.get(i - 1));
            }
            items.set(0, chat);
            return position;
        }
    }

    public int updateChat(Chat updatedChat) {
        Chat chat = itemsSparseArray.get(updatedChat.getId());
        if (chat != null) {
            itemsSparseArray.put(updatedChat.getId(), updatedChat);
            int position = items.indexOf(chat);
            if (position != 0) {
                for (int i = position; i >= 1; i--) {
                    items.set(i, items.get(i - 1));
                }
                items.set(0, updatedChat);
                itemsSparseArray.put(updatedChat.getId(), updatedChat);
            } else {
                items.set(position, updatedChat);
                itemsSparseArray.put(updatedChat.getId(), updatedChat);
            }
            return position;
        }
        return -1;
    }

    public void setTyping(long id) {
        Chat chat = itemsSparseArray.get(id);
        if (chat != null)
            chat.setTyping(true);
    }

    public boolean setUpdating(long id) {
        Chat chat = itemsSparseArray.get(id);
        if (chat != null) {
            chat.setUpdate(true);
            return true;
        }
        return false;
    }

    public void setRead(long id, long[] chatMessagesIds) {
        // TODO
    }

    /*public void reupdateItems(List<Chat> items) {
        if (items != null && items.size() != 0) {
            final int listSize = items.size();
            for (int i = 0; i < listSize; i++) {
                if (this.items.size() > i) {
                    Chat currentChatOfIndex = this.items.get(i);
                    Chat updatedChatOfIndex = items.get(i);
                    if (currentChatOfIndex.getId() != updatedChatOfIndex.getId()
                            || (currentChatOfIndex.getInfo().getLastMessageId() == null && updatedChatOfIndex.getInfo().getLastMessageId() != null)
                            || (currentChatOfIndex.getInfo().getLastMessageId() != null && updatedChatOfIndex.getInfo().getLastMessageId() == null)
                            ||(!currentChatOfIndex.getInfo().getLastMessageId().equals(updatedChatOfIndex.getInfo().getLastMessageId()))) {
                        this.items.set(i, updatedChatOfIndex);
                    }
                }
            }
            *//*this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();*//*

        }
    }*/
}
