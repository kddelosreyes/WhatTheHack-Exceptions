package com.feutech.whatthehack.listeners;

public interface PerformVoteListener {
	public void voteResultPerformed(boolean success, String vote, int post_id);
}
