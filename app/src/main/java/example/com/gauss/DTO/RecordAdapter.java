package example.com.gauss.DTO;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import example.com.gauss.R;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private List<Record> records;
    private int record_res;
    private int record_username;
    private int record_itemname;
    private int record_last_use_time;

    //内部类 为了加载布局的使徒组件
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView itemname;
        TextView last_use_time;
        View record_view; //保存这个只是为了后续添加事件的时候能添加到view上

        public ViewHolder(View view, int name_id, int item_id, int last_use_time_id) {
            super(view);
            record_view = view;
            username = view.findViewById(name_id);
            itemname = view.findViewById(item_id);
            last_use_time = view.findViewById(last_use_time_id);
        }
    }

    public RecordAdapter(List<Record> records) {
        this.records = records;
        record_res = R.layout.record;
        record_username = R.id.record_username;
        record_itemname = R.id.record_itemname;
        record_last_use_time = R.id.record_last_use_time;
    }

    //用于创建ViewHolder实例
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(record_res, parent, false);
        final RecordAdapter.ViewHolder holder = new RecordAdapter.ViewHolder(view, record_username, record_itemname, record_last_use_time);
        //此处可以绑定视图响应事件
        return holder;
    }

    //对holder里的数据项进行赋值
    @Override
    public void onBindViewHolder(RecordAdapter.ViewHolder holder, int position) {
        Record rec = records.get(position);
        if (0 == rec.getState()) {
            holder.username.setText("使用者:" + rec.getUsername());
            holder.itemname.setText("物品:" + rec.getItemname());
            holder.last_use_time.setText("使用时间:" + rec.getLast_use_time());
        } else {
            holder.username.setText("归还者:" + rec.getUsername());
            holder.itemname.setText("物品:" + rec.getItemname());
            holder.last_use_time.setText("归还时间:" + rec.getLast_use_time());
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
