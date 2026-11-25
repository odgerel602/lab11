package mn.edu.must.sqat;

// ЛАБ 11 — хүчтэй болгосон тестийн багц.
// B1: сул assertion-уудыг (assertNotNull, assertTrue(муж)) ЯГ УТГЫН assertEquals болгосон.
// B2: зааг утгын тестүүд нэмсэн (90/89.99, 80/79.99, ..., 3.8/15, 3.4/12, 3.0, gpa 0/4, credits 0).

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    private static final double EPS = 1e-9;

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: муж бүрд ЯГ үсгэн дүнг шалгана ----

    @Test
    void letterGrade_returnsExactGradeForEachRange() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa: ЯГ голчийг шалгана ----

    @Test
    void gpa_returnsExactAverage() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), EPS); // A=4, B=3
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), EPS); // C=2, D=1
        assertEquals(0.0, calc.gpa(new double[]{40}), EPS);     // F=0
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship: дүрэм бүрийн ЯГ хувийг шалгана ----

    @Test
    void scholarship_returnsExactPercentForEachRule() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70, calc.scholarship(3.5, 13, false));
        assertEquals(50, calc.scholarship(3.1, 5, false));
        assertEquals(0, calc.scholarship(2.0, 1, false));
        assertEquals(0, calc.scholarship(4.0, 20, true));   // сахилгын зөрчил
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    // =====================================================================
    // B2: зааг утгын тестүүд
    // =====================================================================

    // ---- letterGrade: заагийн хоёр талын утга ----

    @Test
    void letterGrade_boundaries() {
        assertEquals("A", calc.letterGrade(100));
        assertEquals("A", calc.letterGrade(90));
        assertEquals("B", calc.letterGrade(89.99));
        assertEquals("B", calc.letterGrade(80));
        assertEquals("C", calc.letterGrade(79.99));
        assertEquals("C", calc.letterGrade(70));
        assertEquals("D", calc.letterGrade(69.99));
        assertEquals("D", calc.letterGrade(60));
        assertEquals("F", calc.letterGrade(59.99));
        assertEquals("F", calc.letterGrade(0));
    }

    @Test
    void letterGrade_justOutsideRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-0.01));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(100.01));
    }

    // ---- gpa: switch-ийн бүх үсгэн дүн ----

    @Test
    void gpa_eachLetterGradeHasCorrectPoints() {
        assertEquals(4.0, calc.gpa(new double[]{90}), EPS);
        assertEquals(3.0, calc.gpa(new double[]{80}), EPS);
        assertEquals(2.0, calc.gpa(new double[]{70}), EPS);
        assertEquals(1.0, calc.gpa(new double[]{60}), EPS);
        assertEquals(0.0, calc.gpa(new double[]{59.99}), EPS);
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 30}), EPS); // (4+3+2+1+0)/5
    }

    @Test
    void gpa_invalidScoreInside_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{90, 101}));
    }

    // ---- scholarship: 3.8/15 заагууд ----

    @Test
    void scholarship_fullBoundary_3_8_and_15() {
        assertEquals(100, calc.scholarship(3.8, 15, false));
        assertEquals(70, calc.scholarship(3.79, 15, false));  // голч заагаас доош
        assertEquals(70, calc.scholarship(3.8, 14, false));   // кредит заагаас доош
        assertEquals(70, calc.scholarship(3.5, 16, false));   // зөвхөн кредит хангасан
        assertEquals(70, calc.scholarship(3.9, 13, false));   // зөвхөн голч хангасан
    }

    // ---- scholarship: 3.4/12 заагууд ----

    @Test
    void scholarship_partialBoundary_3_4_and_12() {
        assertEquals(70, calc.scholarship(3.4, 12, false));
        assertEquals(50, calc.scholarship(3.39, 12, false));
        assertEquals(50, calc.scholarship(3.4, 11, false));
        assertEquals(50, calc.scholarship(3.1, 13, false));   // зөвхөн кредит хангасан
        assertEquals(50, calc.scholarship(3.5, 5, false));    // зөвхөн голч хангасан
    }

    // ---- scholarship: 3.0 заагууд ----

    @Test
    void scholarship_minimumBoundary_3_0() {
        assertEquals(50, calc.scholarship(3.0, 0, false));
        assertEquals(0, calc.scholarship(2.99, 20, false));
    }

    // ---- scholarship: оролтын хязгаар (gpa 0 ба 4, credits 0) ----

    @Test
    void scholarship_inputLimits_areValid() {
        assertEquals(0, calc.scholarship(0.0, 0, false));
        assertEquals(0, calc.scholarship(0.0, 20, false));
        assertEquals(100, calc.scholarship(4.0, 20, false));
        assertEquals(50, calc.scholarship(4.0, 0, false));
    }

    @Test
    void scholarship_justOutsideInputLimits_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.01, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.01, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    @Test
    void scholarship_violationAlwaysZero() {
        assertEquals(0, calc.scholarship(3.8, 15, true));
        assertEquals(0, calc.scholarship(3.4, 12, true));
        assertEquals(0, calc.scholarship(3.0, 0, true));
    }
}
