package com.foros.tools;

/**
 *  This utility can execute three commands: "search", "add" and "pairs" (commands are set as the first argument).
 *  <br/><br/>
 *  The command "search" validates and prepares resources for translation.
 *  Using english version of resource we specify what resources were changed, removed, added
 *  and check if corresponding changes were done for another language.
 *  Parameters:
 *    <ol>
 *      <li>dir - resource dir</li>
 *      <li>old-dir - old resource dir (optional)</li>
 *      <li>out - output directory (prepared resources wil be saved)</li>
 *      <li>lang - language (ja, ru, ko, pt, ro, zh, tr)</li>
 *    </ol>
 *  <br/><br/>
 *  The command "add" adds translated resources and sorts changed resources as in english version.
 *  Previously it validates existing resources.
 *  Parameters:
 *    <ol>
 *      <li>dir - resource dir</li>
 *      <li>add-dir - directory with new translated resources</li>
 *      <li>out - output directory (where merged and sorted resources wil be saved)</li>
 *      <li>lang - language (ja, ru, ko, pt, ro, zh, tr)</li>
 *    </ol>
 *  <br/><br/>
 *  The command "pairs" creates a file with all current language translations and their english values after them.
 *  It can be helpful for translators, who wants to inspect current translations.
 *  Parameters:
 *    <ol>
 *      <li>dir - resource dir</li>
 *      <li>out - output directory</li>
 *      <li>lang - language (ja, ru, ko, pt, ro, zh, tr)</li>
 *    </ol>
 *  <br/><br/>
 *  The command "same" searches the resources which have the same value in english, but different translations.
 *  Parameters:
 *    <ol>
 *      <li>dir - resource dir</li>
 *      <li>out - output directory</li>
 *      <li>lang - language (ja, ru, ko, pt, ro, zh, tr)</li>
 *    </ol>
 *  <br/><br/>
 */
public class ResourceUtil {
    public static final String LINE_SEPARATOR = "\n";

    private static final String helpMessage =
        "Usage:\n" +
        "1) search dir=<<resource dir>> [old-dir=<<old resource dir>>] out=<<output dir>> lang=ru|ko|ja|pt|ro|zh|tr\n" +
        "2) add dir=<<resource dir>> add-dir=<<added resource dir>> untranslated-dir=<<dir with resources to translate>> out=<<output dir>> lang=ru|ko|ja|pt|ro|zh|tr\n" +
        "3) add_unsorted dir=<<resource dir>> add-dir=<<added resource dir>> out=<<output dir>> lang=ru|ko|ja|pt|ro|zh|tr\n" +
        "4) pairs dir=<<resource dir>> out=<<output dir>> lang=ru|ko|ja|pt|ro|zh|tr\n" +
        "5) same dir=<<resource dir>> out=<<output dir>> lang=ru|ko|ja|pt|ro|zh|tr\n";

    public void process(String[] args) throws Exception {
        Args arguments = new Args(helpMessage);
        try {
            arguments.parse(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            arguments.printHelp();
            return;
        }

        switch (arguments.getUtil()) {
            case SEARCH: {
                SearchResource res = new SearchResource();
                res.execute(arguments);
                break;
            }

            case ADD: {
                AddResource res = new AddResource();
                res.execute(arguments);
                break;
            }

            case ADD_UNSORTED: {
                AddUnsortedResource res = new AddUnsortedResource();
                res.execute(arguments);
                break;
            }

            case PAIRS: {
                PairsResource res = new PairsResource();
                res.execute(arguments);
                break;
            }

            case SAME: {
                SearchSameResource res = new SearchSameResource();
                res.execute(arguments);
                break;
            }

            default: {
                arguments.printHelp();
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ResourceUtil util = new ResourceUtil();
        util.process(args);
    }
}
