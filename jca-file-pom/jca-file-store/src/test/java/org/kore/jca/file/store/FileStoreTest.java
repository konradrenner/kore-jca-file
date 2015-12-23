/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file.store;

import java.io.Closeable;
import java.io.PrintWriter;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Konrad Renner
 */
public class FileStoreTest {

    DefaultFileConnection cut;
    Closeable closeable;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void initialize() {
        final String directory = root.getRoot().getAbsolutePath();
        this.closeable = mock(Closeable.class);
        this.cut = new DefaultFileConnection(new PrintWriter(System.out), directory, this.closeable);
    }

    @Test
    public void autoClose() throws Exception {
        final String directory = root.getRoot().getAbsolutePath();
        try (DefaultFileConnection bucket = new DefaultFileConnection(new PrintWriter(System.out), directory, this.closeable);) {
            bucket.begin();
        }
        verify(this.closeable).close();
    }

    @Test
    public void writeAndRollback() throws Exception {
        final String key = "hey";
        this.cut.begin();
        final byte[] content = "duke".getBytes();
        this.cut.write(key, content);
        byte[] actual = this.cut.fetch(key);
        assertThat(actual, is(content));
        this.cut.rollback();
        actual = this.cut.fetch(key);
        assertNull(actual);

    }

    @Test
    public void writeAndCommit() throws Exception {
        final String key = "hey";
        this.cut.begin();
        final byte[] content = "duke".getBytes();
        this.cut.write(key, content);
        byte[] actual = this.cut.fetch(key);
        assertThat(actual, is(content));
        this.cut.commit();
        actual = this.cut.fetch(key);
        assertThat(actual, is(content));
    }

    @Test
    public void deleteAndCommit() throws Exception {
        final String key = "hey";
        this.cut.begin();
        final byte[] content = "duke".getBytes();
        this.cut.write(key, content);
        byte[] actual = this.cut.fetch(key);
        assertThat(actual, is(content));
        this.cut.commit();
        this.cut.begin();
        this.cut.delete(key);
        this.cut.commit();
        actual = this.cut.fetch(key);
        assertNull(actual);
    }

    @Test
    public void fetchInTransactionShouldNotTriggerWriteOnCommit() throws Exception {
        final String key = "fetch";

        // given
        final byte[] existingContent = "hello".getBytes();
        this.cut.begin();
        this.cut.write(key, existingContent);
        this.cut.commit();

        // when
        this.cut.begin();
        this.cut.fetch(key);
        this.cut.commit();

        // then
        final byte[] actual = this.cut.fetch(key);
        assertThat(actual, is(existingContent));
    }

    @Test
    public void writeMultipleTimes() throws Exception {
        final String key = "multiWrite";

        this.cut.begin();
        final byte[] firstEntry = "hello ".getBytes();
        this.cut.write(key, firstEntry);

        final byte[] secondEntry = "world!".getBytes();
        this.cut.write(key, secondEntry);
        this.cut.commit();

        final byte[] actual = this.cut.fetch(key);
        assertThat(actual, is("hello world!".getBytes()));
    }

}
