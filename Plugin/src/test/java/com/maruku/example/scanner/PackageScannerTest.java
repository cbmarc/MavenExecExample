package com.maruku.example.scanner;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by marc on 26/11/15.
 */
public class PackageScannerTest {

    @Test
    public void testScanOnlyTest() throws Exception {
        List<Class<?>> classes = PackageScanner.find("com.maruku.example.scanner", PackageScanner.TYPE.TEST);
        Assert.assertTrue(classes.size() == 1);
    }

    @Test
    public void testScanAll() throws Exception {
        List<Class<?>> classes = PackageScanner.find("com.maruku.example.scanner", PackageScanner.TYPE.MAIN);
        Assert.assertTrue(classes.size() > 1);
    }


}
