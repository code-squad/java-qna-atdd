package study.mock_test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MathApplicationTest {
    @InjectMocks
    MathApplication mathApplication = new MathApplication();

    @Mock
    CalculatorService calcService;

    @Test
    public void add() {
        when(calcService.add(10.0, 20.0)).thenReturn(30.00);
        Assert.assertEquals(mathApplication.add(10.0, 20.0), 30.0,0);
        verify(calcService).add(10.0, 20.0);
    }
}
