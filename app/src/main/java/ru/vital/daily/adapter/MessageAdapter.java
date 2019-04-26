package ru.vital.daily.adapter;

import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.adapter.viewholder.ChatDateHeaderViewHolder;
import ru.vital.daily.adapter.viewholder.MessageViewHolder;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Message;

public class MessageAdapter extends BaseAdapter<Message, BaseViewHolder, ViewDataBinding> {

    public final SingleLiveEvent<Message> longClickEvent = new SingleLiveEvent<>();

    private final SparseBooleanArray selectedItems = new SparseBooleanArray();

    private final int START_SIMPLE_VIEW_TYPE = 0,
                    END_SIMPLE_VIEW_TYPE = 1,
                    START_MEDIA_VIEW_TYPE = 2,
                    END_MEDIA_VIEW_TYPE = 3,
                    START_MAP_VIEW_TYPE = 4,
                    END_MAP_VIEW_TYPE = 5,
                    START_LINK_VIEW_TYPE = 6,
                    END_LINK_VIEW_TYPE = 7,
                    START_CALL_VIEW_TYPE = 8,
                    END_CALL_VIEW_TYPE = 9,
                    START_AUDIO_VIEW_TYPE = 10,
                    END_AUDIO_VIEW_TYPE = 11,
                    DATE_VIEW_TYPE = 12;

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
            case START_CALL_VIEW_TYPE:
                return R.layout.item_message_start_call;
            case END_CALL_VIEW_TYPE:
                return R.layout.item_message_end_call;
            case START_AUDIO_VIEW_TYPE:
                return R.layout.item_message_start_audio;
            case END_AUDIO_VIEW_TYPE:
                return R.layout.item_message_end_audio;
            case DATE_VIEW_TYPE:
            default:
                return R.layout.item_message_date_header;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        switch (viewType) {
            case DATE_VIEW_TYPE:
                return new ChatDateHeaderViewHolder(viewDataBinding);
            default:
                MessageViewHolder chatViewHolder = new MessageViewHolder(viewDataBinding);
                chatViewHolder.viewModel.itemLongClickedEvent = longClickEvent;
                return chatViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
