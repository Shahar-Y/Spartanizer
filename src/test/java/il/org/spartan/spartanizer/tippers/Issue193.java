package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.tippers.TrimmerTestsUtils.*;

import org.junit.*;
import org.junit.runners.*;

/** @author Dor Ma'ayan
 * @since 2016-09-25 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) @SuppressWarnings({ "static-method", "javadoc" }) public class Issue193 {
  @Test public void t10() {
    trimmingOf("x*0").gives("0").stays();
  }

  @Test public void t20() {
    trimmingOf("0*x").gives("0").stays();
  }

  @Test public void t30() {
    trimmingOf("(x+y)*0").gives("0").stays();
  }

  @Test public void t40() {
    trimmingOf("calc()*0").gives("0*calc()").stays();
  }

  @Test public void t50() {
    trimmingOf("0*(f())").stays();
  }

  @Test public void t60() {
    trimmingOf("0*(new int[f()])").stays();
  }

  @Test public void t70() {
    trimmingOf("x*0*new int[f()]").gives("0*x*new int[f()]").stays();
  }

  @Test public void t80() {
    trimmingOf("calc()*x*0").gives("0*x*calc()").stays();
  }
}
