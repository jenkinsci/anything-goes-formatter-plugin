package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.markup.MarkupFormatter;
import hudson.markup.MarkupFormatterDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Kohsuke Kawaguchi
 */
public class UnsafeMarkupFormatter extends MarkupFormatter {
    @DataBoundConstructor
    public UnsafeMarkupFormatter() {
    }

    @Override
    public void translate(String markup, Writer output) throws IOException {
        output.write(markup);
    }

    @Extension
    public static class DescriptorImpl extends MarkupFormatterDescriptor {
        @Override
        public String getDisplayName() {
            return "Allows arbitrary HTML including JavaScript (UNSAFE)";
        }
    }
}
