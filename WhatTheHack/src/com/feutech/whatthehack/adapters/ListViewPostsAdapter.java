package com.feutech.whatthehack.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.feutech.whatthehack.AppController;
import com.feutech.whatthehack.R;
import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.database.PostHelper;
import com.feutech.whatthehack.listeners.PerformVoteListener;
import com.feutech.whatthehack.model.Post;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.UserSingleton;

public class ListViewPostsAdapter extends BaseAdapter implements PerformVoteListener{

	private Context context;
	
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	
	private ArrayList<Post> listOfPosts;
	
	private User user;
	
	public ListViewPostsAdapter(Context context, ArrayList<Post> listOfPosts) {
		this.context = context;
		this.listOfPosts = listOfPosts;
		
		imageLoader = AppController.getInstance().getImageLoader();
		
		user = (User) UserSingleton.getInstance().getObject();
	}
	
	@Override
	public int getCount() {
		return listOfPosts.size();
	}

	@Override
	public Object getItem(int position) {
		return listOfPosts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View row, ViewGroup container) {
		
		if (inflater == null)
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		row = inflater.inflate(R.layout.list_item_posts, container, false);
		
		NetworkImageView networkImageViewPlace = (NetworkImageView) row.findViewById(R.id.networkImageViewPlace);
		
		TextView textViewUsername = (TextView) row.findViewById(R.id.textViewUsername);
		TextView textViewAddress = (TextView) row.findViewById(R.id.textViewAddress);
		TextView textViewStatusMessage = (TextView) row.findViewById(R.id.textViewStatusMessage);
		
		ImageView imageViewCategory = (ImageView) row.findViewById(R.id.imageViewCategory);
		TextView downVoteCounter = (TextView) row.findViewById(R.id.downvoteCounter);
		ImageView downVoteCategory = (ImageView) row.findViewById(R.id.downvoteCategory);
		TextView upVoteCounter = (TextView) row.findViewById(R.id.upvoteCounter);
		ImageView upVoteCategory = (ImageView) row.findViewById(R.id.upvoteCategory);
		
		final Post post = listOfPosts.get(position);
		
		networkImageViewPlace.setImageUrl(post.getPhoto(), imageLoader);
		
		textViewUsername.setText(post.getUsername());
		textViewAddress.setText(post.getPlacename());
		textViewStatusMessage.setText(post.getStatusMessage());
		
		int upVoteFlag = post.getUpvote();
		int downVoteFlag = post.getDownvote();
		
		upVoteCategory.setImageResource(upVoteFlag == 1 ? R.drawable.voted_up : R.drawable.vote_up);
		downVoteCategory.setImageResource(downVoteFlag == 1 ? R.drawable.voted_down : R.drawable.vote_down);
		
		upVoteCounter.setText(String.valueOf(post.getTotalUpvote()));
		downVoteCounter.setText(String.valueOf(post.getTotaldownVote()));
		
		upVoteCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobileApi.votePost(user.getUsername(), post.getPost_id(), "upvote", ListViewPostsAdapter.this);
			}
		});
		
		downVoteCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobileApi.votePost(user.getUsername(), post.getPost_id(), "downvote", ListViewPostsAdapter.this);
			}
		});
		
		return row;
	}

	private void updateList(String vote, int post_id) {
		PostHelper helper = new PostHelper(context);
		helper.open();
		helper.updateVotes(post_id, vote);
		
		listOfPosts = helper.getAllPosts();
		helper.close();
		
		notifyDataSetChanged();
	}

	@Override
	public void voteResultPerformed(String text, boolean success, String vote, int post_id) {
		if (success) {
			updateList(vote, post_id);
		} else {}
	}
}