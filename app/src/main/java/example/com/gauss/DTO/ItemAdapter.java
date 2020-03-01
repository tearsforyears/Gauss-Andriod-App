package example.com.gauss.DTO;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import example.com.gauss.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private int item_res;
    private int item_name;
    private int item_amount;
    private int item_description;
    private int item_user;

    //内部类 为了加载布局的使徒组件
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView amount;
        TextView description;
        TextView user;
        View item_view; //保存这个只是为了后续添加事件的时候能添加到view上

        public ViewHolder(View view, int name_id, int amount_id, int description_id, int user_id) {
            super(view);
            item_view = view;
            name = view.findViewById(name_id);
            amount = view.findViewById(amount_id);
            description = view.findViewById(description_id);
            user = view.findViewById(user_id);
        }
    }


    public ItemAdapter(List<Item> items, int item_name, int item_amount, int item_description, int item_user) {
        this.items = items;
        this.item_name = item_name;
        this.item_amount = item_amount;
        this.item_description = item_description;
        this.item_user = item_user;
    }

    public ItemAdapter(List<Item> items) {
        this.items = items;
        item_res = R.layout.item;
        item_name = R.id.item_item_name;
        item_amount = R.id.item_amount;
        item_description = R.id.item_description;
        item_user = R.id.item_user;
    }

    //用于创建ViewHolder实例
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_res, parent, false);
        final ViewHolder holder = new ViewHolder(view, item_name, item_amount, item_description, item_user);
        //此处可以绑定视图响应事件
        return holder;
    }

    //对holder里的数据项进行赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.name.setText("物品名:" + item.getItemname());
        holder.amount.setText("总数:" + item.getAmount());
        holder.description.setText("描述:" + item.getDescription());
        holder.user.setText("登记者:" + item.getUsername());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
