package tsuteto.unfavmod;

import org.apache.logging.log4j.Level;
import twitter4j.*;

public class ModUserStreamListener implements UserStreamListener
{

    private Twitter twitter;

    public ModUserStreamListener(Twitter twitter)
    {
        this.twitter = twitter;
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0)
    {

    }

    @Override
    public void onScrubGeo(long arg0, long arg1)
    {

    }

    @Override
    public void onStallWarning(StallWarning arg0)
    {

    }

    @Override
    public void onStatus(Status stat)
    {

    }

    @Override
    public void onTrackLimitationNotice(int arg0)
    {

    }

    @Override
    public void onException(Exception arg0)
    {

    }

    @Override
    public void onBlock(User arg0, User arg1)
    {

    }

    @Override
    public void onDeletionNotice(long arg0, long arg1)
    {

    }

    @Override
    public void onDirectMessage(DirectMessage arg0)
    {

    }

    @Override
    public void onFavorite(User arg0, User arg1, Status arg2)
    {

    }

    @Override
    public void onFollow(User arg0, User arg1)
    {

    }

    @Override
    public void onUnfollow(User user, User user2)
    {

    }

    @Override
    public void onFriendList(long[] arg0)
    {

    }

    @Override
    public void onUnblock(User arg0, User arg1)
    {

    }

    @Override
    public void onUnfavorite(User arg0, User arg1, Status arg2)
    {
        ModLog.debug("Unfavorited!");
        ModLog.debug("arg0: " + arg0.getScreenName());
        ModLog.debug("arg1: " + arg1.getScreenName());

        try
        {
            if (!twitter.getScreenName().equals(arg0.getScreenName())
                    && twitter.getScreenName().equals(arg1.getScreenName()))
            {
                ModLog.debug("@" + arg0.getScreenName() + " " + arg0.getName() + "にあんふぁぼされた");
                UnfavInfo unfav = new UnfavInfo();
                unfav.name = arg0.getName();
                unfav.screenName = arg0.getScreenName();
                unfav.unfavdName = arg1.getName();
                unfav.unfavdScreenName = arg1.getScreenName();

                synchronized (UnfavDamageHandler.unfavQueue)
                {
                    UnfavDamageHandler.unfavQueue.offer(unfav);
                }
            }
        }
        catch (Exception e)
        {
            ModLog.log(Level.WARN, e, "onUnfavorite error");
        }
    }

    @Override
    public void onUserListCreation(User arg0, UserList arg1)
    {

    }

    @Override
    public void onUserListDeletion(User arg0, UserList arg1)
    {

    }

    @Override
    public void onUserListMemberAddition(User arg0, User arg1, UserList arg2)
    {

    }

    @Override
    public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2)
    {

    }

    @Override
    public void onUserListSubscription(User arg0, User arg1, UserList arg2)
    {

    }

    @Override
    public void onUserListUnsubscription(User arg0, User arg1, UserList arg2)
    {

    }

    @Override
    public void onUserListUpdate(User arg0, UserList arg1)
    {

    }

    @Override
    public void onUserProfileUpdate(User arg0)
    {

    }

}
