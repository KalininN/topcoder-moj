package moj;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import moj.CPPHarnessGenerator.TestCodeGenerationState;
import moj.mocks.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.InvalidTypeException;

@RunWith(Parameterized.class)
public class CPPParameterTest {
    private String typename, value, expected;

    public CPPParameterTest(String typename, String varval, String expected) {
        this.typename = typename;
        this.value = varval;
        this.expected = expected;
    }

    CPPHarnessGenerator generator;
    TestCodeGenerationState code;

    @Before public void setUp() {
        generator = new CPPHarnessGenerator(
                new ProblemComponentModelMock(),
                CPPLanguage.CPP_LANGUAGE,
                new PreferencesMock()
                );
        code = new TestCodeGenerationState();
    }

    static String compressSpaceBeforeEquals(String str) {
        return str.replaceFirst("\\s*=", " =").trim();
    }

    @Parameters
    public static List<Object[]> data() {
        Object[][] data = new Object[][] { 
                {"int", "-2147483648", "int var = -2147483648;"},

                {"String", "\"test string\"", "string var = \"test string\";"},
                {"String", "\"   multiple  spaces  \"", "string var = \"   multiple  spaces  \";"},
                {"String", "\"  a  =  3  \"", "string var = \"  a  =  3  \";"},

                {"double", "1.345e08", "double var = 1.345e08;"},

                {"long", "123",         "long long var = 123;"},
                {"long", "-9999999999", "long long var = -9999999999LL;"},
                {"long", "2147483648",  "long long var = 2147483648LL;"},

                {"int[]", "{-2147483648, 2147483647, 0, -1, 555}", "vector<int> var = {-2147483648, 2147483647, 0, -1, 555};"},
                {"int[]", "{}",  "vector<int> var = {};"},

                {"String[]", "{\"a\",\n \"\",\n \"test test\"}", "vector<string> var = {\"a\",\n \"\",\n \"test test\"};"},
                {"String[]", "{\"spaces  space\", \"a   a\"}", "vector<string> var = {\"spaces  space\", \"a   a\"};"},

                {"double[]", "{ 1e9, -3.e-012, -4, 5 }", "vector<double> var = { 1e9, -3.e-012, -4, 5 };"},

                {"long[]", "{ 0, -1, 1, 2147483648,\n-2147483649, 9223372036854775807, -9223372036854775808}", "vector<long long> var = {0, -1, 1, 2147483648LL, -2147483649LL, 9223372036854775807LL, -9223372036854775808LL};"},
        };
        return Arrays.asList(data);
    }

    @Test public void test() throws InvalidTypeException {
        DataType dt = DataTypeFactoryMock.getDataType(this.typename);
        generator.generateParameter(code, dt, "var", this.value, false);
        String result = compressSpaceBeforeEquals(code.lines.get(0).toString());
        assertEquals(this.expected, result);
    }
}
