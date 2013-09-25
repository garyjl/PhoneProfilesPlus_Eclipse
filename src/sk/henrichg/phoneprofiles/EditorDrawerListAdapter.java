package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EditorDrawerListAdapter extends BaseAdapter {

    Context context;
    ListView listView;
    String[] drawerItemsTitle;
    String[] drawerItemsSubtitle;
    
    public EditorDrawerListAdapter(ListView listView, Context context, 
    								String[] itemTitle, String[] itemSubtitle)
    {
        this.context = context;
        this.listView = listView;
        this.drawerItemsTitle = itemTitle;
        this.drawerItemsSubtitle = itemSubtitle;
    }
    
	public int getCount() {
		return drawerItemsTitle.length;
	}

	public Object getItem(int position) {
		return drawerItemsTitle[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	static class ViewHolder {
		  TextView itemTitle;
		  TextView itemSubtitle;
		  int position;
		}
	

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		View vi = convertView;
        if (convertView == null)
        {
      		LayoutInflater inflater = LayoutInflater.from(context);
    	    //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.editor_drawer_list_item, parent, false); 
    	    		
            holder = new ViewHolder();
            holder.itemTitle = (TextView)vi.findViewById(R.id.editor_drawer_list_item_title);
            holder.itemSubtitle = (TextView)vi.findViewById(R.id.editor_drawer_list_item_subtitle);
            vi.setTag(holder);        
        }
        else
        {
      	    holder = (ViewHolder)vi.getTag();
        }
        
       	holder.itemTitle.setText(drawerItemsTitle[position]);
    	holder.itemSubtitle.setText(drawerItemsSubtitle[position]);

        if (android.os.Build.VERSION.SDK_INT < 11)
        {
        	if (listView.isItemChecked(position))
        	{
        		holder.itemTitle.setTypeface(null,Typeface.BOLD);
        		holder.itemSubtitle.setTypeface(null,Typeface.BOLD);
        	}
        	else
        	{
        		holder.itemTitle.setTypeface(null,Typeface.NORMAL);
        		holder.itemSubtitle.setTypeface(null,Typeface.NORMAL);
        	}
        }
    	
        return vi;	
    }

}
