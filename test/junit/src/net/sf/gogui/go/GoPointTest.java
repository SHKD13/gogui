//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.go;

import java.util.ArrayList;

public class GoPointTest
    extends junit.framework.TestCase
{
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite()
    {
        return new junit.framework.TestSuite(GoPointTest.class);
    }

    public void testCompareTo()
    {
        assertEquals(0, getPoint(5, 5).compareTo(getPoint(5, 5)));
        assertEquals(-1, getPoint(5, 5).compareTo(getPoint(6, 5)));
        assertEquals(-1, getPoint(5, 5).compareTo(getPoint(5, 6)));
        assertEquals(1, getPoint(5, 5).compareTo(getPoint(4, 5)));
        assertEquals(1, getPoint(5, 5).compareTo(getPoint(5, 4)));
    }

    public void testDirection()
    {
        checkPoint(getPoint(5, 5).up(11), 5, 6);
        checkPoint(getPoint(5, 5).down(), 5, 4);
        checkPoint(getPoint(5, 5).left(), 4, 5);
        checkPoint(getPoint(5, 5).right(13), 6, 5);
        checkPoint(getPoint(6, 0).down(), 6, 0);
        checkPoint(getPoint(6, 9).up(9), 6, 9);
        checkPoint(getPoint(0, 21).left(), 0, 21);
        checkPoint(getPoint(19, 5).right(19), 19, 5);
    }

    public void testToString()
    {
        assertEquals(GoPoint.toString((GoPoint)null), "PASS");
        assertEquals(GoPoint.toString(getPoint(0, 0)), "A1");
        assertEquals(getPoint(7, 4).toString(), "H5");
        assertEquals(getPoint(8, 4).toString(), "J5");
        assertEquals(getPoint(18, 18).toString(), "T19");
        assertEquals(getPoint(9, 20).toString(), "K21");
        assertEquals(getPoint(9, 20).toString(), "K21");
        assertEquals(GoPoint.toString((ArrayList)null), "(null)");
        ArrayList v = new ArrayList();
        assertEquals(GoPoint.toString(v), "");
        v.add(getPoint(0, 0));
        assertEquals(GoPoint.toString(v), "A1");
        v.add(getPoint(3, 4));
        assertEquals(GoPoint.toString(v), "A1 D5");
        v.add(getPoint(0, 18));
        assertEquals(GoPoint.toString(v), "A1 D5 A19");
    }

    public void testParse() throws GoPoint.InvalidPoint
    {
        checkPoint(GoPoint.parsePoint("A1", 19), 0, 0);
        checkPoint(GoPoint.parsePoint(" T19 ", 19), 18, 18);
        checkPoint(GoPoint.parsePoint("J3  ", 19), 8, 2);
        checkPoint(GoPoint.parsePoint("b17", 19), 1, 16);
        assertNull(GoPoint.parsePoint("PASS", 19));
        assertNull(GoPoint.parsePoint("pass", 19));
        checkInvalid("11", 19);
        checkInvalid("19Z", 19);
        checkInvalid("A100", 25);
        checkInvalid("C10", 9);
        ArrayList pointListArrayList
            = GoPoint.parsePointListArrayList("  R15 PASS T19 ", 19);
        assertEquals(pointListArrayList.size(), 3);
        checkPoint((GoPoint)pointListArrayList.get(0), 16, 14);
        assertNull((GoPoint)pointListArrayList.get(1));
        checkPoint((GoPoint)pointListArrayList.get(2), 18, 18);
        GoPoint[] pointList = GoPoint.parsePointList("PASS A1", 9);
        assertEquals(pointList.length, 2);
        assertNull(pointList[0]);
        checkPoint(pointList[1], 0, 0);
    }

    public void testUnique()
    {
        checkPoint(getPoint(5, 5), 5, 5);
        checkPoint(getPoint(23, 23), 23, 23);
    }

    private GoPoint getPoint(int x, int y)
    {
        return GoPoint.get(x, y);
    }

    private void checkInvalid(String string, int boardSize)
    {
        try
        {
            GoPoint.parsePoint(string, boardSize);
            fail();
        }
        catch (GoPoint.InvalidPoint e)
        {
        }
    }

    private void checkPoint(GoPoint point, int x, int y)
    {
        assertSame(point, GoPoint.get(x, y));
        assertEquals(point.getX(), x);
        assertEquals(point.getY(), y);
    }
}

