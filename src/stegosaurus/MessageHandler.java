/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegosaurus;

/**
 * Worries about the logic of dealing with the user's messages.
 * @author joe
 */
public class MessageHandler {
    /**
     * The message this object manages.
     */
    private String msg;
    /**
     * Start a new message handler to worry about msg.
     * @param msg the message to concern this handler with.
     */
    public MessageHandler(String msg) {
        this.msg = msg;
    }
    
    /**
     * Get the message handled by this object as a byte array, where each byte
     * is a char inside the message string.
     * @return the array of bytes representing the message.
     */
    public byte[] AsByteArray() {
        int l = msg.length();
        byte[] retval = new byte[l + 4];
        for (int i = 0; i < 4; i++) {
            retval[i] = (byte) ((l & (0xFF << (8 * i))) >> (8 * i));
        }
        for (int i = 0; i < msg.length(); i++) {
            retval[i + 4] = (byte) msg.charAt(i);
        }
        return retval;
    }
}
