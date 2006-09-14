/* FileName: it/di/unipi/iochatto/chat/ChatDialog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;

import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.util.JxtaBiDiPipe;


/**
 * A class to display the chat session user interface and handle the pipes 
 * used to send and receive chat messages.
 */
public class ChatDialog extends JFrame implements ActionListener, 
    KeyListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4101797925437706865L;

	/**
     * The text area used to enter a chat message to send to a remote user.
     */
    private JTextArea message = new JTextArea(3, 20);

    /**
     * The text area to show the incoming and outgoing chat messages in 
     * the conversation.
     */
    private JTextArea conversation = new JTextArea(12, 20);

    /**
     * The input pipe being used to receive chat messages.
     */
    private InputPipe inputPipe = null;

    /**
     * The output pipe being used to send chat messages.
     */
    private JxtaBiDiPipe outputPipe = null;

    /**
     * The name of the remote buddy in the conversation.
     */
    private String buddyName = null;

    /**
     * The name of the local user in the conversation.
     */
    private String displayName = null;

    /**
     * The pipe service to use to create Message objects.
     */
    private PipeService pipe = null;    

    /**
     * A thread to handle receiving messages and updating the user 
     * interface.
     */    
    private MessageReader reader = null;
    
    /**
     * A handler class to deal with closing the window.
     */
    public class WindowHandler extends WindowAdapter
    {
        /**
         * Handles the window closing.
         *
         * @param   e the object with details of the window event.
         */    
        public void windowClosing(WindowEvent e)
        {
            if (reader != null)
            {
                reader.stop();
            }
            
            setVisible(false);
        }
    }

    /**
     * A simple thread to handle reading messages from the input pipe and 
     * updating the user interface.
     */
    public class MessageReader extends Thread
    {
        /**
         * The main thread loop.
         */
        public void run()
        {
            while (true)
            {
                try
                {
                    Message messageObj = inputPipe.waitForMessage();

                    // Make sure that the dialog is visible.
                    setVisible(true);
                     
                    // Extract the Chat Message.
                    StringMessageElement me = (StringMessageElement)  messageObj.getMessageElement("ChatMessage"); 
                    StringBuffer chatMessage = 
                        new StringBuffer(me.toString());
                    
                    // Update the user interface.
                    StringBuffer conversationText = 
                        new StringBuffer(conversation.getText());
                    conversationText.append("\n");
                    conversationText.append(buddyName).append("> ");
                    conversationText.append(chatMessage);
                    conversation.setText(conversationText.toString());
                }
                catch (Exception e)
                {
                    System.out.println("Error...: " + e);
                }
            }
        }
    }

    /**
     * Create a new window to handle a conversation with a remote user.
     *
     * @param   buddyName the display name for the remote user in the 
     *          chat session.
     * @param   displayName the display name for the local user in the 
     *          chat session.
     * @param   pipe the pipe service to use to create messages.
     * @param   inputPipe the pipe to use to receive messages.
     * @param   outputPipe the pipe to use to send messages.
     */
    public ChatDialog(String buddyName, String displayName, 
        PipeService pipe, InputPipe inputPipe, JxtaBiDiPipe outputPipe)
    {
        super();

        this.pipe = pipe;
        this.inputPipe = inputPipe;
        this.outputPipe = outputPipe;
        this.buddyName = buddyName;
        this.displayName = displayName;

        // Initialize the user interface.
        initializeUserInterface();

        // Set the title of the dialog.
        setTitle("Conversation - " + buddyName);
        
        reader = new MessageReader();
        reader.start();
    }

    /**
     * Handles the "Send" button.
     *
     * @param   e the event corresponding to the button being pressed.
     */
    public void actionPerformed(ActionEvent e)
    {
        sendMessage();
    }

    /**
     * Initializes the dialog's user interface.
     */
    public void initializeUserInterface()
    {
        Container framePanel = getContentPane();
        JPanel conversationPanel = new JPanel();
        JPanel sendPanel = new JPanel();
        JButton sendButton = new JButton("Send!");
        GridBagLayout layout = new GridBagLayout();
        JScrollPane    messagePane = new JScrollPane(message);

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        layout.addLayoutComponent(messagePane, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 0.1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        layout.addLayoutComponent(sendButton, constraints);
        
        sendPanel.setLayout(layout);
        sendPanel.setBorder(BorderFactory.createTitledBorder(
            "Compose A Message:"));
        sendPanel.add(messagePane);
        sendPanel.add(sendButton);
    
        conversationPanel.setLayout(new BorderLayout());
        conversationPanel.setBorder(
            BorderFactory.createTitledBorder("Conversation:"));
        conversationPanel.add(new JScrollPane(conversation),
            BorderLayout.CENTER);
        conversation.setEditable(false);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        layout.addLayoutComponent(conversationPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.BOTH;
        layout.addLayoutComponent(sendPanel, constraints);
            
        framePanel.setLayout(layout);
        framePanel.add(conversationPanel);
        framePanel.add(sendPanel);

        sendButton.addActionListener(this);
        message.addKeyListener(this);
        
        pack();
    }

    /**
     * Invoked when a key has been pressed.
     *
     * @param   e the event describing the key event.
     */
    public void keyPressed(KeyEvent e)
    {
        // Do nothing. Only need keyReleased method from KeyListener.
    }

    /**
     * Invoked when a key has been released.
     *
     * @param   e the event describing the key event.
     */
    public void keyReleased(KeyEvent e)
    {
        // Handle the user pressing Return in the message composition 
        // text area.
        if (KeyEvent.VK_ENTER == e.getKeyCode())
        {
            sendMessage();
        }
    }

    /**
     * Invoked when a key has been typed.
     *
     * @param   e the event describing the key event.
     */
    public void keyTyped(KeyEvent e)
    {
        // Do nothing. Only need keyReleased method from KeyListener.
    }

    /**
     * Send the message in the message composition text area to the 
     * remote user.
     */
    public void sendMessage()
    {
        StringBuffer conversationText = 
            new StringBuffer(conversation.getText());
        String messageString = message.getText();

        // Make sure that there is something to send!
        if ((null != messageString) && (0 < messageString.length()))
        {
            // Create a new message object.
        	StringMessageElement strMsgElement = new StringMessageElement("ChatMessage",messageString,null);
        
            Message messageObj = new Message();
            

            // Send the message using the output pipe.
            messageObj.addMessageElement(strMsgElement);

            // Send the message.
            try
            {	
            	outputPipe.sendMessage(messageObj);
            }
            catch (IOException e2)
            {
                System.out.println("Error sending..." + e2);
            }

            // Update the user interface.
            conversationText.append("\n");
            conversationText.append(displayName).append("> ");
            conversationText.append(messageString);
            conversation.setText(conversationText.toString());
            message.setText("");
        }
    }
}