package com.technovision.civilization.threading.tasks;

import com.technovision.civilization.queries.command.CommandQuery;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.entity.Player;

public class PlayerQuestionTask extends QuestionBaseTask implements Runnable {

    Player askedPlayer; /* player who is being asked a question. */
    Player questionPlayer; /* player who has asked the question. */
    String question; /* Question being asked. */
    long timeout; /* Timeout after question expires. */
    CommandQuery finishedFunction;

    protected String response = ""; /* Response to the question. */
    protected Boolean responded = false; /*Question was answered. */

    public PlayerQuestionTask() { }

    public PlayerQuestionTask(Player askedplayer, Player questionplayer, String question, long timeout, CommandQuery finishedFunction) {
        this.askedPlayer = askedplayer;
        this.questionPlayer = questionplayer;
        this.question = question;
        this.timeout = timeout;
        this.finishedFunction = finishedFunction;
    }

    @Override
    public void run() {
        CivMessage.send(askedPlayer, CivColor.LightGray+"Question from: "+CivColor.LightBlue+questionPlayer.getName());
        CivMessage.send(askedPlayer, CivColor.LightPurple+CivColor.BOLD+question);
        CivMessage.send(askedPlayer, CivColor.LightGray+"Respond by typing "+CivColor.LightBlue+"/accept"+CivColor.LightGray+" or "+CivColor.LightBlue+"/deny");
        try {
            synchronized(this) {
                this.wait(timeout);
            }
        } catch (InterruptedException e) {
            cleanup();
            return;
        }
        if (responded) {
            finishedFunction.processResponse(response);
            cleanup();
            return;
        }
        CivMessage.send(askedPlayer, CivColor.LightGray+"You failed to respond to the question from "+questionPlayer.getName()+" in time.");
        CivMessage.send(questionPlayer, CivColor.LightGray+askedPlayer.getName()+" failed to answer the question in time.");
        cleanup();
    }

    public Boolean getResponded() {
        synchronized(responded) {
            return responded;
        }
    }

    public void setResponded(Boolean response) {
        synchronized(this.responded) {
            this.responded = response;
        }
    }

    public String getResponse() {
        synchronized(response) {
            return response;
        }
    }

    public void setResponse(String response) {
        synchronized(this.response) {
            setResponded(true);
            this.response = response;
        }
    }

    /* When this task finishes, remove itself from the hashtable. */
    private void cleanup() {
        CivMessage.removeQuestion(askedPlayer.getName());
    }
}
