package com.feutech.whatthehack.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.feutech.whatthehack.R;
import com.feutech.whatthehack.adapters.ListViewPostsAdapter;
import com.feutech.whatthehack.database.PostHelper;
import com.feutech.whatthehack.model.Post;

public class PostsListViewFragment extends Fragment {

	private View view;
	
	private ArrayList<Post> listOfPosts;
	
	private ListView listViewPosts;
	
	private ListViewPostsAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_post_list, container, false);
	
		PostHelper helper = new PostHelper(getActivity());
		helper.open();
		listOfPosts = helper.getAllPosts();
		helper.close();
		
		listViewPosts = (ListView) view.findViewById(R.id.listViewPosts);
		
		adapter = new ListViewPostsAdapter(getActivity(), listOfPosts);
		
		listViewPosts.setAdapter(adapter);
		
		return view;
	}
}
