//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gtpregress;

import java.io.PrintStream;
import java.util.ArrayList;
import net.sf.gogui.util.Options;
import net.sf.gogui.util.StringUtil;
import net.sf.gogui.version.Version;

/** GtpRegress main function. */
public final class Main
{
    public static void main(String[] args)
    {
        try
        {
            String options[] = {
                "config:",
                "help",
                "long",
                "output:",
                "verbose",
                "version"
            };
            Options opt = Options.parse(args, options);
            if (opt.isSet("help"))
            {
                printUsage(System.out);
                return;
            }
            if (opt.isSet("version"))
            {
                System.out.println("GtpRegress " + Version.get());
                return;
            }
            boolean verbose = opt.isSet("verbose");
            boolean longOutput = opt.isSet("long");
            String output = opt.getString("output", "");
            ArrayList arguments = opt.getArguments();
            int size = arguments.size();
            if (size < 2)
            {
                printUsage(System.err);
                System.exit(-1);
            }
            String program = (String)arguments.get(0);
            String tests[] = new String[size - 1];
            for (int i = 0; i <  size - 1; ++i)
                tests[i] = (String)arguments.get(i + 1);
            GtpRegress gtpRegress =
                new GtpRegress(program, tests, output, longOutput, verbose);
            System.exit(gtpRegress.getResult() ? 0 : 1);
        }
        catch (Throwable t)
        {
            StringUtil.printException(t);
            System.exit(2);
        }
    }

    /** Make constructor unavailable; class is for namespace only. */
    private Main()
    {
    }

    private static void printUsage(PrintStream out)
    {
        out.print("Usage: java -jar gtpregress.jar [options] program test.tst"
                  + " [...]\n" +
                  "\n" +
                  "-config       Config file\n" +
                  "-help         Display this help and exit\n" +
                  "-long         Longer output to standard out\n" +
                  "-output       Output directory\n" +
                  "-verbose      Log GTP stream to stderr\n" +
                  "-version      Display this help and exit\n");
    }
}

