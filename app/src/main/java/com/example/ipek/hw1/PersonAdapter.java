package com.example.ipek.hw1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

    public class PersonAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Person> personList;
        private int row_layout;

        public PersonAdapter(Activity activity, int row_layout,List<Person> personList) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.personList = personList;
            this.row_layout=row_layout;
        }

        @Override
        public int getCount() {
            return personList.size();
        }

        @Override
        public Person getItem(int position) {

            return personList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;

            rowView = inflater.inflate(row_layout, null);

            TextView textView =(TextView) rowView.findViewById(R.id.name);
            ImageView imageView =(ImageView) rowView.findViewById(R.id.image);
            TextView textView1= (TextView) rowView.findViewById(R.id.phoneNumber);

            Person person = personList.get(position);

            textView.setText(person.getName());

            textView1.setText(person.getPhoneNumber());

            imageView.setImageResource(R.drawable.defaultphoto);

            return rowView;
        }
    }


