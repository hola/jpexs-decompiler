/*
 *  Copyright (C) 2010-2016 JPEXS, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.decompiler.flash;

import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.helpers.CodeFormatting;
import com.jpexs.decompiler.flash.helpers.HighlightedTextWriter;
import com.jpexs.decompiler.flash.tags.DoActionTag;
import com.jpexs.decompiler.flash.tags.ShowFrameTag;
import com.jpexs.decompiler.flash.tags.Tag;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author JPEXS
 */
public class ActionScript2Test extends ActionScript2TestBase {

    @BeforeClass
    public void init() throws IOException, InterruptedException {
        //Main.initLogging(false);
        Configuration.autoDeobfuscate.set(false);
        Configuration.simplifyExpressions.set(false);
        Configuration.decompile.set(true);
        Configuration.registerNameFormat.set("_loc%d_");
        swf = new SWF(new BufferedInputStream(new FileInputStream("testdata/as2/as2.swf")), false);
    }

    private void compareSrc(int frame, String expectedResult) {
        DoActionTag doa = getFrameSource(frame);
        assertNotNull(doa);
        HighlightedTextWriter writer = new HighlightedTextWriter(new CodeFormatting(), false);
        try {
            Action.actionsToSource(doa, doa.getActions(), "", writer);
        } catch (InterruptedException ex) {
            fail();
        }
        String actualResult = cleanPCode(writer.toString());
        expectedResult = cleanPCode(expectedResult);
        assertEquals(actualResult, expectedResult);

    }

    private DoActionTag getFrameSource(int frame) {
        int f = 0;
        DoActionTag lastDoa = null;

        for (Tag t : swf.getTags()) {
            if (t instanceof DoActionTag) {
                lastDoa = (DoActionTag) t;
            }
            if (t instanceof ShowFrameTag) {
                f++;
                if (f == frame) {
                    return lastDoa;
                }
                lastDoa = null;
            }
        }
        return null;
    }

    @Test
    public void frame23_Test() {
        compareSrc(23, "stop();\r\n"
        );
    }

    @Test
    public void frame24_unicodeTest() {
        compareSrc(24, "trace(\"unicodeTest\");\r\n"
                + "var k = \"\u05d4\u05d9\u05e4\u05d5\u05e4\u05d5\u05d8\u05de\u05d9, \u05d0\u05d5 \u05d0\";\r\n"
                + "trace(k);\r\n"
        );
    }

    @Test
    public void frame25_ifWithElseTest() {
        compareSrc(25, "trace(\"ifWithElseTest\");\r\n"
                + "var i = 5;\r\n"
                + "if(i == 258)\r\n"
                + "{\r\n"
                + "trace(\"onTrue\");\r\n"
                + "}\r\n"
                + "else\r\n"
                + "{\r\n"
                + "trace(\"onFalse\" + i);\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame26_forTest() {
        compareSrc(26, "trace(\"forTest\");\r\n"
                + "var i = 0;\r\n"
                + "while(i < 10)\r\n"
                + "{\r\n"
                + "trace(\"hello:\" + i);\r\n"
                + "i++;\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame27_whileTest() {
        compareSrc(27, "trace(\"whileTest\");\r\n"
                + "var i = 0;\r\n"
                + "while(i < 10)\r\n"
                + "{\r\n"
                + "trace(\"hello:\" + i);\r\n"
                + "i++;\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame28_forWithContinueTest() {
        compareSrc(28, "trace(\"forWithContinueTest\");\r\n"
                + "var i = 0;\r\n"
                + "for(; i < 10; i++)\r\n"
                + "{\r\n"
                + "trace(\"hello:\" + i);\r\n"
                + "if(i == 5)\r\n"
                + "{\r\n"
                + "trace(\"i==5\");\r\n"
                + "if(i == 7)\r\n"
                + "{\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "trace(\"hawk\");\r\n"
                + "}\r\n"
                + "trace(\"end of the loop\");\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame29_doWhileTest() {
        compareSrc(29, "trace(\"doWhileTest\");\r\n"
                + "var i = 0;\r\n"
                + "do\r\n"
                + "{\r\n"
                + "trace(\"i=\" + i);\r\n"
                + "i++;\r\n"
                + "}\r\n"
                + "while(i < 10);\r\n"
                + "trace(\"end\");\r\n"
        );
    }

    @Test
    public void frame30_switchTest() {
        compareSrc(30, "trace(\"switchTest\");\r\n"
                + "var i = 5;\r\n"
                + "switch(i)\r\n"
                + "{\r\n"
                + "case 0:\r\n"
                + "case 1:\r\n"
                + "trace(\"one\");\r\n"
                + "break;\r\n"
                + "case 2:\r\n"
                + "trace(\"two\");\r\n"
                + "case 3:\r\n"
                + "trace(\"three\");\r\n"
                + "break;\r\n"
                + "case 4:\r\n"
                + "trace(\"four\");\r\n"
                + "break;\r\n"
                + "default:\r\n"
                + "trace(\"default clause\");\r\n"
                + "}\r\n"
                + "trace(\"scriptend\");\r\n"
        );
    }

    @Test
    public void frame31_strictEqualsTest() {
        compareSrc(31, "trace(\"strictEqualsTest\");\r\n"
                + "var i = 5;\r\n"
                + "if(i === 5)\r\n"
                + "{\r\n"
                + "trace(\"equals strict\");\r\n"
                + "}\r\n"
                + "if(i !== 5)\r\n"
                + "{\r\n"
                + "trace(\"not equals strict\");\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame32_switchForTest() {
        compareSrc(32, "trace(\"switchForTest\");\r\n"
                + "var i = 0;\r\n"
                + "for(; i < 10; i++)\r\n"
                + "{\r\n"
                + "switch(i)\r\n"
                + "{\r\n"
                + "case 0:\r\n"
                + "trace(\"zero\");\r\n"
                + "continue;\r\n"
                + "case 5:\r\n"
                + "trace(\"five\");\r\n"
                + "break;\r\n"
                + "case 10:\r\n"
                + "trace(\"ten\");\r\n"
                + "break;\r\n"
                + "case 1:\r\n"
                + "if(i == 7)\r\n"
                + "{\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "trace(\"one\");\r\n"
                + "default:\r\n"
                + "trace(\"def\");\r\n"
                + "}\r\n"
                + "trace(\"before loop end\");\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame33_functionTest() {
        compareSrc(33, "function hello(what, second)\r\n"
                + "{\r\n"
                + "trace(\"hello \" + what + \"! \" + second);\r\n"
                + "}\r\n"
                + "trace(\"functionTest\");\r\n"
                + "hello(\"friend\",7);\r\n"
        );
    }

    @Test
    public void frame34_multipleConditionsTest() {
        compareSrc(34, "trace(\"multipleConditionsTest\");\r\n"
                + "var k = 5;\r\n"
                + "if(k == 7 && k == 8)\r\n"
                + "{\r\n"
                + "trace(\"first\");\r\n"
                + "}\r\n"
                + "if(k == 9)\r\n"
                + "{\r\n"
                + "trace(\"second\");\r\n"
                + "}\r\n"
                + "trace(\"finish\");\r\n"
        );
    }

    @Test
    public void frame35_multipleConditions2Test() {
        compareSrc(35, "trace(\"multipleConditions2Test\");\r\n"
                + "var k = 5;\r\n"
                + "if(k == 7 && k == 8)\r\n"
                + "{\r\n"
                + "trace(\"first\");\r\n"
                + "}\r\n"
                + "if(k == 9 || k == 6)\r\n"
                + "{\r\n"
                + "trace(\"second\");\r\n"
                + "}\r\n"
                + "trace(\"finish\");\r\n"
        );
    }

    @Test
    public void frame36_chainedAssignmentsTest() {
        compareSrc(36, "trace(\"chainedAssignmentsTest\");\r\n"
                + "var a = 7;\r\n"
                + "var b = 8;\r\n"
                + "var c = 9;\r\n"
                + "var d = c = b = a = 10;\r\n"
                + "trace(d);\r\n"
        );
    }

    @Test
    public void frame37_objectsTest() {
        compareSrc(37, "trace(\"objectsTest\");\r\n"
                + "var flashBox = new Box(box1);\r\n"
                + "_root.onEnterFrame = function()\r\n"
                + "{\r\n"
                + "flashBox.moveUp();\r\n"
                + "};\r\n"
                + "var ship = new Ship(200);\r\n"
                + "var enemy = new Enemy(56);\r\n"
                + "ship.moveDown(0.5);\r\n"
                + "ship.moveUp(0.2);\r\n"
                + "enemy.moveRight(230);\r\n"
                + "enemy.moveLeft(100);\r\n"
                + "var mt = new com.jpexs.MyTest();\r\n"
                + "mt.test();\r\n"
                + "var c = new Cox(box1);\r\n"
        );
    }

    @Test
    public void frame38_doWhile2Test() {
        compareSrc(38, "trace(\"doWhile2Test\");\r\n"
                + "var k = 5;\r\n"
                + "do\r\n"
                + "{\r\n"
                + "k++;\r\n"
                + "if(k == 7)\r\n"
                + "{\r\n"
                + "k = 5 * k;\r\n"
                + "}\r\n"
                + "else\r\n"
                + "{\r\n"
                + "k = 5 + k;\r\n"
                + "}\r\n"
                + "}\r\n"
                + "while(k < 9);\r\n"
        );
    }

    @Test
    public void frame39_whileAndTest() {
        compareSrc(39, "trace(\"whileAndTest\");\r\n"
                + "var a = 5;\r\n"
                + "var b = 10;\r\n"
                + "while(a < 10 && b > 1)\r\n"
                + "{\r\n"
                + "a++;\r\n"
                + "b--;\r\n"
                + "}\r\n"
                + "a = 7;\r\n"
                + "b = 9;\r\n"
        );
    }

    @Test
    public void frame40_forInTest() {
        compareSrc(40, "function testForIn()\r\n"
                + "{\r\n"
                + "var _loc1_ = [];\r\n"
                + "for(var _loc2_ in _loc1_)\r\n"
                + "{\r\n"
                + "if(_loc2_ > 3)\r\n"
                + "{\r\n"
                + "if(_loc2_ == 5)\r\n"
                + "{\r\n"
                + "return 7;\r\n"
                + "}\r\n"
                + "return 8;\r\n"
                + "}\r\n"
                + "}\r\n"
                + "}\r\n"
                + "trace(\"forInTest\");\r\n"
                + "trace(testForIn());\r\n"
                + "var arr = [];\r\n"
                + "for(var a in arr)\r\n"
                + "{\r\n"
                + "trace(a);\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame41_tryTest() {
        compareSrc(41, "trace(\"tryTest\");\r\n"
                + "var k = 5;\r\n"
                + "try\r\n"
                + "{\r\n"
                + "k = Infinity;\r\n"
                + "}\r\n"
                + "catch(e)\r\n"
                + "{\r\n"
                + "trace(\"bug \" + e);\r\n"
                + "}\r\n"
                + "finally\r\n"
                + "{\r\n"
                + "trace(\"huu\");\r\n"
                + "}\r\n"
                + "trace(\"next\");\r\n"
                + "try\r\n"
                + "{\r\n"
                + "k = 6;\r\n"
                + "}\r\n"
                + "catch(e)\r\n"
                + "{\r\n"
                + "trace(\"bug2 \" + e);\r\n"
                + "}\r\n"
                + "trace(\"next2\");\r\n"
                + "var k = 5;\r\n"
                + "try\r\n"
                + "{\r\n"
                + "k = Infinity;\r\n"
                + "}\r\n"
                + "finally\r\n"
                + "{\r\n"
                + "trace(\"final\");\r\n"
                + "}\r\n"
                + "trace(\"end\");\r\n"
        );
    }

    @Test
    public void frame42_indicesTest() {
        compareSrc(42, "trace(\"indicesTest\");\r\n"
                + "var k = [1,2,3];\r\n"
                + "var b = k[1];\r\n"
                + "trace(b);\r\n"
        );
    }

    @Test
    public void frame43_incDecTest() {
        compareSrc(43, "function tst()\r\n"
                + "{\r\n"
                + "return 1;\r\n"
                + "}\r\n"
                + "trace(\"incDecTest\");\r\n"
                + "var i = 5;\r\n"
                + "var b = i++;\r\n"
                + "var c = --i + 5;\r\n"
                + "trace(\"a:\" + a + \" b:\" + b + \" c:\" + c);\r\n"
                + "var arr = [1,2,3];\r\n"
                + "arr[tst()]++;\r\n"
                + "var d = arr[tst()];\r\n"
                + "trace(d);\r\n"
        );
    }

    @Test
    public void frame44_chainedAssignments2Test() {
        compareSrc(44, "trace(\"chainedAssignments2Test\");\r\n"
                + "var a = 5;\r\n"
                + "var b = 6;\r\n"
                + "var c = 7;\r\n"
                + "var d = c = b = a = 4;\r\n"
                + "if((d = c = b = a = 7) > 2)\r\n"
                + "{\r\n"
                + "trace(d);\r\n"
                + "}\r\n"
                + "trace(d + 1);\r\n"
                + "var i = 0;\r\n"
                + "while(i < 5)\r\n"
                + "{\r\n"
                + "if(i == 7)\r\n"
                + "{\r\n"
                + "}\r\n"
                + "i++;\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame45_function2Test() {
        compareSrc(45, "function a()\r\n"
                + "{\r\n"
                + "trace(\"hi\");\r\n"
                + "var _loc1_ = 5;\r\n"
                + "if(_loc1_ == 7)\r\n"
                + "{\r\n"
                + "return undefined;\r\n"
                + "}\r\n"
                + "_loc1_ = _loc1_ * 9;\r\n"
                + "trace(_loc1_);\r\n"
                + "}\r\n"
                + "trace(\"function2Test\");\r\n"
        );
    }

    @Test
    public void frame46_tryFunctionTest() {
        compareSrc(46, "function testtry()\r\n"
                + "{\r\n"
                + "var _loc1_ = 5;\r\n"
                + "try\r\n"
                + "{\r\n"
                + "if(_loc1_ == 3)\r\n"
                + "{\r\n"
                + "return undefined;\r\n"
                + "}\r\n"
                + "if(_loc1_ == 4)\r\n"
                + "{\r\n"
                + "throw new Error();\r\n"
                + "}\r\n"
                + "else\r\n"
                + "{\r\n"
                + "_loc1_ = 7;\r\n"
                + "}\r\n"
                + "}\r\n"
                + "catch(e)\r\n"
                + "{\r\n"
                + "trace(\"error\");\r\n"
                + "}\r\n"
                + "finally\r\n"
                + "{\r\n"
                + "trace(\"finally\");\r\n"
                + "}\r\n"
                + "}\r\n"
                + "trace(\"tryFunctionTest\");\r\n"
        );
    }

    @Test
    public void frame47_ternarTest() {
        compareSrc(47, "trace(\"ternarTest\");\r\n"
                + "var a = 5;\r\n"
                + "var b = a != 4?3:2;\r\n"
                + "trace(b);\r\n"
        );
    }

    @Test
    public void frame48_forInInTest() {
        compareSrc(48, "function tst()\r\n"
                + "{\r\n"
                + "var _loc2_ = [];\r\n"
                + "_loc2_[0] = [];\r\n"
                + "for(var _loc3_ in _loc2_)\r\n"
                + "{\r\n"
                + "for(var _loc1_ in _loc3_)\r\n"
                + "{\r\n"
                + "if(_loc1_ == 5)\r\n"
                + "{\r\n"
                + "return 5;\r\n"
                + "}\r\n"
                + "}\r\n"
                + "if(_loc3_ == 8)\r\n"
                + "{\r\n"
                + "return 3;\r\n"
                + "}\r\n"
                + "}\r\n"
                + "return 8;\r\n"
                + "}\r\n"
                + "trace(\"forInInTest\");\r\n"
                + "tst();\r\n"
        );
    }

    @Test
    public void frame49_registersFuncTest() {
        compareSrc(49, "function tst(px)\r\n"
                + "{\r\n"
                + "var _loc1_ = 57;\r\n"
                + "_loc1_ = _loc1_ * 27;\r\n"
                + "}\r\n"
                + "trace(\"registersFuncTest\");\r\n"
                + "tst(5);\r\n"
                + "var s = String(5);\r\n"
        );
    }

    @Test
    public void frame50_ifFrameLoadedTest() {
        compareSrc(50, "trace(\"ifFrameLoadedTest\");\r\n"
                + "ifFrameLoaded(9)\r\n"
                + "{\r\n"
                + "trace(\"loaded\");\r\n"
                + "}\r\n"
        );
    }

    @Test
    public void frame51_function3Test() {
        compareSrc(51, "function tst()\r\n"
                + "{\r\n"
                + "var _loc1_ = 5;\r\n"
                + "c = _loc1_ = 8;\r\n"
                + "trace(\"hi\");\r\n"
                + "trace(_loc1_);\r\n"
                + "if((e = d = f = c = 9) > 5)\r\n"
                + "{\r\n"
                + "trace(\"dd\");\r\n"
                + "}\r\n"
                + "}\r\n"
                + "trace(\"function3Test\");\r\n"
                + "var c = 7;\r\n"
                + "var d = 7;\r\n"
                + "var e = 8;\r\n"
                + "tst();\r\n"
        );
    }

    @Test
    public void frame52_commaOperatorTest() {
        compareSrc(52, "trace(\"commaOperatorTest\");\r\n"
                + "var a = 0;\r\n"
                + "var b = 0;\r\n"
                + "var c = 0;\r\n"
                + "while(true)\r\n"
                + "{\r\n"
                + "a++;\r\n"
                + "b = b + 2;\r\n"
                + "if(c < 10)\r\n"
                + "{\r\n"
                + "trace(c);\r\n"
                + "c++;\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "break;\r\n"
                + "}\r\n"
                + "trace(\"konec\");\r\n"
        );
    }

    @Test
    public void frame53_commaOperator2Test() {
        compareSrc(53, "trace(\"commaOperator2Test\");\r\n"
                + "var k = 8;\r\n"
                + "do\r\n"
                + "{\r\n"
                + "if(k == 9)\r\n"
                + "{\r\n"
                + "trace(\"h\");\r\n"
                + "if(k == 9)\r\n"
                + "{\r\n"
                + "trace(\"f\");\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "trace(\"b\");\r\n"
                + "}\r\n"
                + "trace(\"gg\");\r\n"
                + "}\r\n"
                + "while(k++, k < 10);\r\n"
                + "trace(\"ss\");\r\n"
        );
    }

    @Test
    public void frame54_function4Test() {
        compareSrc(54, "function tst()\r\n"
                + "{\r\n"
                + "var _loc1_ = 5;\r\n"
                + "while(_loc1_ < 10)\r\n"
                + "{\r\n"
                + "if(_loc1_ == 5)\r\n"
                + "{\r\n"
                + "if(_loc1_ == 6)\r\n"
                + "{\r\n"
                + "return true;\r\n"
                + "}\r\n"
                + "_loc1_ = _loc1_ + 1;\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "return false;\r\n"
                + "}\r\n"
                + "}\r\n"
                + "trace(\"function4Test\");\r\n"
                + "tst();\r\n"
        );
    }

    @Test
    public void frame55_pushTest() {
        compareSrc(55, "trace(\"pushTest\");\r\n"
                + "53;"
        );
    }

    @Test
    public void frame56_commaOperator3Test() {
        compareSrc(56, "trace(\"commaOperator3Test\");\r\n"
                + "var k = 1;\r\n"
                + "while(true)\r\n"
                + "{\r\n"
                + "k++;\r\n"
                + "if(k < 10)\r\n"
                + "{\r\n"
                + "k = k * 5;\r\n"
                + "trace(k);\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "break;\r\n"
                + "}\r\n"
                + "trace(\"end\");\r\n"
        );
    }

    @Test
    public void frame57_commaOperator4Test() {
        compareSrc(57, "trace(\"commaOperator4Test\");\r\n"
                + "var k = 0;\r\n"
                + "do\r\n"
                + "{\r\n"
                + "trace(k);\r\n"
                + "if(k == 8)\r\n"
                + "{\r\n"
                + "trace(\"a\");\r\n"
                + "if(k == 9)\r\n"
                + "{\r\n"
                + "continue;\r\n"
                + "}\r\n"
                + "trace(\"d\");\r\n"
                + "trace(\"b\");\r\n"
                + "}\r\n"
                + "k++;\r\n"
                + "}\r\n"
                + "while(k = k + 5, k < 20);\r\n"
                + "trace(\"end\");\r\n"
        );
    }

    @Test
    public void frame58_globalFunctionsTest() {
        compareSrc(58, "function tst(p1)\r\n"
                + "{\r\n"
                + "trace(\"test\");\r\n"
                + "}\r\n"
                + "trace(\"globalFunctionsTest\");\r\n"
                + "var k = Array(1,2,3);\r\n"
                + "var a = 1;\r\n"
                + "var b = Boolean(a);\r\n"
                + "call(5);\r\n"
                + "var c = \"A\";\r\n"
                + "clearInterval(5);\r\n"
                + "clearTimeout(4);\r\n"
                + "var mc;\r\n"
                + "duplicateMovieClip(mc,\"copy\",16389);\r\n"
                + "a = escape(\"how\");\r\n"
                + "var f = a;\r\n"
                + "fscommand(\"alert(\\\"hi\\\");\");\r\n"
                + "a = mc._alpha;\r\n"
                + "a = getTimer();\r\n"
                + "getURL(\"http://localhost/\",\"wnd\",\"POST\");\r\n"
                + "a = getVersion();\r\n"
                + "gotoAndPlay(5);\r\n"
                + "gotoAndStop(8);\r\n"
                + "ifFrameLoaded(4)\r\n"
                + "{\r\n"
                + "trace(\"loaded\");\r\n"
                + "}\r\n"
                + "a = int(f);\r\n"
                + "a = isFinite(f);\r\n"
                + "a = isNaN(f);\r\n"
                + "a = length(f);\r\n"
                + "loadMovie(\"http://localhost/test.swf\",a,\"GET\");\r\n"
                + "loadMovieNum(\"http://localhost/test.swf\",5,\"GET\");\r\n"
                + "loadVariables(\"http://localhost/vars.txt\",a,\"GET\");\r\n"
                + "loadVariablesNum(\"http://localhost/vars.txt\",4,\"GET\");\r\n"
                + "a = mbchr(f);\r\n"
                + "a = mblength(f);\r\n"
                + "a = mbord(f);\r\n"
                + "a = mbsubstring(\"aaaa\",5,4);\r\n"
                + "MMExecute(\"destroyPC\");\r\n"
                + "nextFrame();\r\n"
                + "gotoAndStop(1);\r\n"
                + "a = Number(f);\r\n"
                + "a = Object(f);\r\n"
                + "a = ord(f);\r\n"
                + "a = parseFloat(f);\r\n"
                + "a = parseInt(f,16);\r\n"
                + "play();\r\n"
                + "prevFrame();\r\n"
                + "gotoAndStop(1);\r\n"
                + "print(mc,\"bframe\");\r\n"
                + "printAsBitmap(mc,\"bframe\");\r\n"
                + "printAsBitmapNum(5,\"bframe\");\r\n"
                + "printNum(4,\"bframe\");\r\n"
                + "a = random(10);\r\n"
                + "removeMovieClip(mc);\r\n"
                + "setInterval(tst,5,f);\r\n"
                + "mc._alpha = 25;\r\n"
                + "setTimeout(tst,5,f);\r\n"
                + "showRedrawRegions(false,0);\r\n"
                + "startDrag(mc,1,5,5,6,6);\r\n"
                + "stop();\r\n"
                + "stopAllSounds();\r\n"
                + "stopDrag();\r\n"
                + "a = String(f);\r\n"
                + "a = \"aa\";\r\n"
                + "targetPath(f);\r\n"
                + "tellTarget(mc)\r\n"
                + "{\r\n"
                + "trace(\"told\");\r\n"
                + "}\r\n"
                + "toggleHighQuality();\r\n"
                + "a = unescape(f);\r\n"
                + "unloadMovie(mc);\r\n"
                + "unloadMovieNum(4);\r\n"
                + "updateAfterEvent();\r\n"
        );
    }

    @Test
    public void frame59_unaryOpTest() {
        compareSrc(59, "trace(\"unaryOpTest\");\r\n"
                + "var a = 5;\r\n"
                + "var c = ~a;\r\n"
                + "var d = ~(a + c);\r\n"
                + "var e = - c;\r\n"
        );
    }

    @Test
    public void frame61_switchDefaultTest() {
        compareSrc(61, "trace(\"switchDefaultTest\");\r\n"
                + "var k = 5;\r\n"
                + "switch(k)\r\n"
                + "{\r\n"
                + "case 5:\r\n"
                + "default:\r\n"
                + "trace(\"default 5\");\r\n"
                + "case 6:\r\n"
                + "trace(\"default 5,6\");\r\n"
                + "break;\r\n"
                + "case 7:\r\n"
                + "trace(\"7\");\r\n"
                + "}\r\n"
                + "trace(\"afterSwitch\");\r\n"
        );
    }

    @Test
    public void frame62_Test() {
        compareSrc(62, "trace(\"switchDefaultTest2\");\r\n"
                + "var k = 5;\r\n"
                + "switch(k)\r\n"
                + "{\r\n"
                + "case 5:\r\n"
                + "trace(\"5\");\r\n"
                + "break;\r\n"
                + "default:\r\n"
                + "trace(\"default\");\r\n"
                + "case 6:\r\n"
                + "trace(\"default, 6\");\r\n"
                + "break;\r\n"
                + "case 7:\r\n"
                + "trace(\"7\");\r\n"
                + "}\r\n"
                + "trace(\"afterSwitch\");\r\n"
        );
    }
}
