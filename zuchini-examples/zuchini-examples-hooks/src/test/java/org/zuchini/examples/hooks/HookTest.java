package org.zuchini.examples.datatables;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;

@RunWith(Zuchini.class)
@ZuchiniOptions(stepDefinitionPackages = {"org.zuchini.examples"}, featurePackages = {"features"}, reportIndividualSteps = true)
public class HookTest {
}
