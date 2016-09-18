package il.org.spartan.spartanizer.wrings;

import static il.org.spartan.spartanizer.wrings.TrimmerTestsUtils.*;

import org.junit.*;

/** @author Stav Namir
 * @since 2016-08-29 */
@SuppressWarnings("static-method") @Ignore("Issue #97 : under construction") public final class Issue097Test {
  @Test public void test01() {
    trimmingOf(
        "\"Spartanizing '\" + javaProject.getElementName() + \"' project \\n\" + \n\"Completed in \" + (1 + i) + \" passes. \\n\" + \n\"Total changes: \" + (initialCount - finalCount) + \"\\n\" + \n\"Suggestions before: \" + initialCount + \"\\n\" + \n\"Suggestions after: \" + finalCount + \"\\n\" + \nmessage")
            // .to("\"Spartanizing '\"+javaProject.getElementName()+\"' project
            // \n\"+\"Completed in \"+(1+i)+\" passes. \n\"+\"Total
            // changes:\"+(initialCount-finalCount)+\"\n\"+\"Suggestions
            // before:\"+initialCount+\"\n\"+\"Suggestions
            // after:\"+finalCount+\"\n\"+message");
            .stays();
    /* supposed to be: "Spartanizing '" + javaProject.getElementName() +
     * "' project \n" + // "Completed in " + (1 + i) + " passes. \n" + //
     * "Total changes: " + (initialCount - finalCount) + "\n" + //
     * "Suggestions before: " + initialCount + "\n" + // "Suggestions after: " +
     * finalCount + "\n" + // message */
  }
}