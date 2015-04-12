package com.feutech.whatthehack;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.database.PostHelper;
import com.feutech.whatthehack.listeners.PerformVoteListener;
import com.feutech.whatthehack.model.Post;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.PostSingleton;
import com.feutech.whatthehack.utilities.UserSingleton;

public class PostDetailsActivity extends Activity implements PerformVoteListener{
	
	private ImageLoader imageLoader;
	
	private Post post;
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_detail_post);
	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		getActionBar().setTitle("Post Details");
		
		NetworkImageView networkImageViewPlace = (NetworkImageView)  findViewById(R.id.networkImageViewPlace);

		TextView textViewUsername = (TextView)  findViewById(R.id.textViewUsername);
		TextView textViewAddress = (TextView)  findViewById(R.id.textViewAddress);
		TextView textViewStatusMessage = (TextView)  findViewById(R.id.textViewStatusMessage);
		
		ImageView imageViewCategory = (ImageView)  findViewById(R.id.imageViewCategory);
		TextView downVoteCounter = (TextView)  findViewById(R.id.downvoteCounter);
		ImageView downVoteCategory = (ImageView)  findViewById(R.id.downvoteCategory);
		TextView upVoteCounter = (TextView)  findViewById(R.id.upvoteCounter);
		ImageView upVoteCategory = (ImageView)  findViewById(R.id.upvoteCategory);
	
		imageLoader = AppController.getInstance().getImageLoader();
		
		post = (Post) PostSingleton.getInstance().getObject();
		user = (User) UserSingleton.getInstance().getObject();
		
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
				MobileApi.votePost(user.getUsername(), post.getPost_id(), "upvote", PostDetailsActivity.this);
			}
		});
		
		downVoteCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobileApi.votePost(user.getUsername(), post.getPost_id(), "downvote", PostDetailsActivity.this);
			}
		});
	}

	private void updateDB(String vote, int post_id) {
		PostHelper helper = new PostHelper(this);
		helper.open();
		helper.updateVotes(post_id, vote);
		helper.close();
		
		finish();
	}
	
	@Override
	public void voteResultPerformed(String text, boolean success, String vote,
			int post_id) {
		updateDB(vote, post_id);
	}
}
