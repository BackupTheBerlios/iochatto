/* FileName: it/di/unipi/iochatto/util/GenerateID.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.util;

import net.jxta.id.IDFactory;

import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;


/**
 * A simple application to generate a Module Class ID, Module Specification
 * ID, Peer Group ID, and Module Specification ID based on the standard 
 * peer group Module Class ID.
 */
public class GenerateID
{
    /**
     * Generates the IDs.
     *
     * @param   args the command-line arguments. Ignored by this app.
     */
    public static void main(String[] args)
    {
        // Create an entirely new Module Class ID.
        ModuleClassID classID = IDFactory.newModuleClassID();

        // Create a Module Specification ID based on the generated 
        // Module Class ID.
        ModuleSpecID specID = IDFactory.newModuleSpecID(classID);

        // Create an entirely new Peer Group ID.
        PeerGroupID groupID = IDFactory.newPeerGroupID();

        // Create a Module Specification ID based on the peer group 
        // Module Class ID.
        ModuleSpecID groupSpecID = IDFactory.newModuleSpecID(
            PeerGroup.allPurposePeerGroupSpecID.getBaseClass());

        // Print out the generated IDs.
        System.out.println("Module Class ID: " + classID.toString());
        System.out.println("Module Spec ID: " + specID.toString());
        System.out.println("Peer Group ID: " + groupID.toString());
        System.out.println("Peer Group Module Spec ID: " 
            + groupSpecID.toString());
    }
}