//=============================================================================
// $Id$
// $Source$
//=============================================================================

import javax.swing.*;
import gtp.*;

//=============================================================================

class CommandThread
    extends Thread
{
    public CommandThread(Gtp gtp)
    {
        m_gtp = gtp;
    }
    
    /** Get answer to asynchronous command.
        You must call getException() first.
    */
    public String getAnswer()
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(! m_commandInProgress);
        return m_answer;
    }
    
    /** Get exception of asynchronous command.
        You must call this before you are allowed to send new a command.
    */
    public Gtp.Error getException()
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(m_commandInProgress);
        m_commandInProgress = false;
        return m_exception;
    }
    
    public String getProgramCommand()
    {
        assert(SwingUtilities.isEventDispatchThread());
        return m_gtp.getProgramCommand();
    }

    public boolean isProgramDead()
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(! m_commandInProgress);
        return m_gtp.isProgramDead();
    }

    public void run()
    {
        try
        {
            synchronized (this)
            {
                while (true)
                {
                    wait();
                    m_answer = null;
                    m_exception = null;
                    try
                    {
                        m_answer = m_gtp.sendCommand(m_command);
                    }
                    catch (Gtp.Error e)
                    {
                        m_exception = e;
                    }
                    SwingUtilities.invokeLater(m_callback);
                }
            }
        }
        catch (InterruptedException e)
        {
            System.err.println("Interrupted.");
            System.exit(-1);
        }
    }
    
    /** Send asynchronous command. */
    public void sendCommand(String command, Runnable callback)
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(! m_commandInProgress);
        synchronized (this)
        {
            m_command = command;
            m_callback = callback;
            m_commandInProgress = true;
            notifyAll();
        }
    }
    
    public String sendCommand(String command) throws Gtp.Error
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(! m_commandInProgress);
        return m_gtp.sendCommand(command);
    }

    public String sendCommand(String command, long timeout) throws Gtp.Error
    {
        assert(SwingUtilities.isEventDispatchThread());
        assert(! m_commandInProgress);
        return m_gtp.sendCommand(command, timeout);
    }

    private boolean m_commandInProgress;
    private Gtp m_gtp;
    private Gtp.Error m_exception;
    private static JFrame m_mainFrame;
    private Runnable m_callback;
    private String m_command;
    private String m_answer;
    private Thread m_commandThread;
}

//=============================================================================
