package org.jenkinsci.plugins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import hudson.markup.MarkupFormatter;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class UnsafeMarkupFormatterTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    // -------------------------------------------------------------------------
    // translate() — unit-level behaviour
    // -------------------------------------------------------------------------

    @Test
    public void translatePassesThroughPlainText() throws IOException {
        StringWriter out = new StringWriter();
        newFormatter().translate("hello world", out);
        assertThat(out.toString(), is("hello world"));
    }

    @Test
    public void translatePassesThroughHtmlMarkup() throws IOException {
        StringWriter out = new StringWriter();
        String input = "<b>bold</b> and <em>italic</em>";
        newFormatter().translate(input, out);
        assertThat(out.toString(), is(input));
    }

    @Test
    public void translatePassesThroughScriptTags() throws IOException {
        // The whole point of this formatter is that it passes everything through
        // unchanged — including potentially unsafe script tags.
        StringWriter out = new StringWriter();
        String input = "<script>alert('xss')</script>";
        newFormatter().translate(input, out);
        assertThat(out.toString(), is(input));
    }

    @Test
    public void translateHandlesNullWithoutWriting() throws IOException {
        StringWriter out = new StringWriter();
        newFormatter().translate(null, out);
        assertThat(out.toString(), is(""));
    }

    @Test
    public void translateHandlesEmptyString() throws IOException {
        StringWriter out = new StringWriter();
        newFormatter().translate("", out);
        assertThat(out.toString(), is(""));
    }

    // -------------------------------------------------------------------------
    // Descriptor — integration
    // -------------------------------------------------------------------------

    @Test
    public void descriptorDisplayNameIsCorrect() {
        UnsafeMarkupFormatter.DescriptorImpl d = new UnsafeMarkupFormatter.DescriptorImpl();
        assertThat(d.getDisplayName(), is("Allows arbitrary HTML including JavaScript (UNSAFE)"));
    }

    @Test
    public void pluginIsRegisteredAsMarkupFormatter() {
        // Verify that the @Extension is picked up and registered with Jenkins.
        UnsafeMarkupFormatter.DescriptorImpl descriptor = j.jenkins.getDescriptorByType(UnsafeMarkupFormatter.DescriptorImpl.class);
        assertThat("UnsafeMarkupFormatter descriptor must be registered", descriptor, notNullValue());
    }

    @Test
    public void formatterCanBeSetOnJenkinsInstance() throws Exception {
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
