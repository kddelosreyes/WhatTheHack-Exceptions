package com.feutech.whatthehack.listeners;

public interface PerformVoteListener {
	public void voteResultPerformed(String text, boolean success, String vote, int post_id);
}
