
package interfaces;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface InterestBearing {

    BigDecimal payMonthlyInterest(YearMonth month);

}
