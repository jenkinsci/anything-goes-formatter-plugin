package org.jenkinsci.plugins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import hudson.markup.MarkupFormatter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class UnsafeMarkupFormatterTest {

    private JenkinsRule j;

    @BeforeEach
    void beforeEach(JenkinsRule rule) {
        j = rule;
    }

    // -------------------------------------------------------------------------
    // translate() — unit-level behaviour
    // -------------------------------------------------------------------------

    @Test
    void translatePassesThroughPlainText() throws Exception {
        StringWriter out = new StringWriter();
        newFormatter().translate("hello world", out);
        assertThat(out.toString(), is("hello world"));
    }

    @Test
    void translatePassesThroughHtmlMarkup() throws Exception {
        StringWriter out = new StringWriter();
        String input = "<b>bold</b> and <em>italic</em>";
        newFormatter().translate(input, out);
        assertThat(out.toString(), is(input));
    }

    @Test
    void translatePassesThroughScriptTags() throws Exception {
        // The whole point of this formatter is that it passes everything through
        // unchanged — including potentially unsafe script tags.
        StringWriter out = new StringWriter();
        String input = "<script>alert('xss')</script>";
        newFormatter().translate(input, out);
        assertThat(out.toString(), is(input));
    }

    @Test
    void translateHandlesNullWithoutWriting() throws Exception {
        StringWriter out = new StringWriter();
        newFormatter().translate(null, out);
        assertThat(out.toString(), is(""));
    }

    @Test
    void translateHandlesEmptyString() throws Exception {
        StringWriter out = new StringWriter();
        newFormatter().translate("", out);
        assertThat(out.toString(), is(""));
    }

    // -------------------------------------------------------------------------
    // Descriptor — integration
    // -------------------------------------------------------------------------

    @Test
    void descriptorDisplayNameIsCorrect() {
        UnsafeMarkupFormatter.DescriptorImpl d = new UnsafeMarkupFormatter.DescriptorImpl();
        assertThat(d.getDisplayName(), is("Allows arbitrary HTML including JavaScript (UNSAFE)"));
    }

    @Test
    void pluginIsRegisteredAsMarkupFormatter() {
        // Verify that the @Extension is picked up and registered with Jenkins.
        UnsafeMarkupFormatter.DescriptorImpl descriptor = j.jenkins.getDescriptorByType(UnsafeMarkupFormatter.DescriptorImpl.class);
        assertThat("UnsafeMarkupFormatter descriptor must be registered", descriptor, notNullValue());
    }

    @Test
    void formatterCanBeSetOnJenkinsInstance() {
        UnsafeMarkupFormatter formatter = newFormatter();
        j.jenkins.setMarkupFormatter(formatter);
        MarkupFormatter active = j.jenkins.getMarkupFormatter();
        assertThat(active, notNullValue());
        assertThat(active.getClass().getName(), is(UnsafeMarkupFormatter.class.getName()));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static UnsafeMarkupFormatter newFormatter() {
        return new UnsafeMarkupFormatter();
    }
}
