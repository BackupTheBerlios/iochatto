/* FileName: it/di/unipi/iochatto/presence/resolver/PresenceStatusListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence.resolver;





/**
 * An interface to encapsulate an object that listens for notification
 * from the PresenceService of newly arrived presence information.
 */
public interface PresenceStatusListener
{
    /**
     * Notify the listener of newly arrived presence information.
     *
     * @param   presenceInfo the newly received presence information.
     */
    public void fireStatusUpdate(PresenceStatusEvent ev);
}

