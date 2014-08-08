package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAResource;
import com.contentful.java.model.CDASyncedSpace;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for consuming the Sync API.
 */
public class SynchronizationTest extends AbsTestCase {
    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void testSynchronization() throws Exception {
        TestCallback<CDASyncedSpace> callback = new TestCallback<CDASyncedSpace>();

        CDAClient client = TestClientFactory.newInstance()
                .setClient(new MockClient("result_test_sync_initial.json"))
                .build();

        // #1 - perform initial synchronization
        client.performInitialSynchronization(callback);
        callback.await();
        verifyResultNotEmpty(callback);
        CDASyncedSpace firstResult = callback.value;
        verifySynchronizationFirst(firstResult);

        // #2 - get delta update
        callback = new TestCallback<CDASyncedSpace>();

        client = TestClientFactory.newInstance()
                .setClient(new MockClient("result_test_sync_update.json"))
                .build();

        client.performSynchronization(firstResult, callback);
        callback.await();
        verifyResultNotEmpty(callback);
        CDASyncedSpace secondResult = callback.value;
        verifySynchronizationSecond(secondResult);

        // #3 - empty update
        callback = new TestCallback<CDASyncedSpace>();

        client = TestClientFactory.newInstance()
                .setClient(new MockClient("result_test_sync_update_empty.json"))
                .build();

        client.performSynchronization(secondResult, callback);
        callback.await();
        verifyResultNotEmpty(callback);
        CDASyncedSpace thirdResult = callback.value;
        verifySynchronizationThird(thirdResult);
    }

    @Test
    public void testSynchronizationBlocking() throws Exception {
        CDAClient client = TestClientFactory.newInstance()
                .setClient(new MockClient("result_test_sync_initial.json"))
                .build();

        // #1 - perform initial synchronization
        CDASyncedSpace firstResult = client.performInitialSynchronizationBlocking();
        verifySynchronizationFirst(firstResult);

        // #2 - get delta update
        client = TestClientFactory.newInstance()
                .setClient(new MockClient("result_test_sync_update.json"))
                .build();

        CDASyncedSpace secondResult = client.performSynchronizationBlocking(firstResult);
        verifySynchronizationSecond(secondResult);

        // #3 - empty update
        client = TestClientFactory.newInstance()
                .setClient(new MockClient("result_test_sync_update_empty.json"))
                .build();

        CDASyncedSpace thirdResult = client.performSynchronizationBlocking(secondResult);
        verifySynchronizationThird(thirdResult);
    }

    @Test
    public void testSynchronizationByToken() throws Exception {
        CDAClient client = TestClientFactory.newInstance().build();

        CDASyncedSpace result = client.performInitialSynchronizationBlocking();
        assertNotNull(result);

        String syncToken = result.getSyncToken();
        assertNotNull(syncToken);

        result = client.performSynchronization(syncToken);
        assertNotNull(result);
    }

    void verifySynchronizationFirst(CDASyncedSpace result) {
        assertNotNull(result);

        ArrayList<CDAResource> items = result.getItems();
        assertEquals(3, items.size());

        CDAEntry entry = (CDAEntry) items.get(0);
        assertEquals("Yiltiquoar", entry.getFields().get("name"));
        assertEquals(Double.valueOf(9999), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(1);
        assertEquals("Tzayclibbon", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2405), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(2);
        assertEquals("Za'ha'zah", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2789), entry.getFields().get("age"));

        assertEquals("FAKE", result.getSyncToken());
    }

    @SuppressWarnings("UnnecessaryBoxing")
    void verifySynchronizationSecond(CDASyncedSpace result) {
        assertNotNull(result);

        ArrayList<CDAResource> items = result.getItems();
        assertEquals(3, items.size());

        CDAEntry entry = (CDAEntry) items.get(0);
        assertEquals("Ooctaiphus", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(1);
        assertEquals("Yiltiquoar", entry.getFields().get("name"));
        assertEquals(Double.valueOf(666666), entry.getFields().get("age"));

        entry = (CDAEntry) items.get(2);
        assertEquals("Za'ha'zah", entry.getFields().get("name"));
        assertEquals(Double.valueOf(2789), entry.getFields().get("age"));

        assertEquals("FAKE", result.getSyncToken());
    }

    void verifySynchronizationThird(CDASyncedSpace result) {
        assertNotNull(result);

        assertEquals(3, result.getItems().size());
        assertEquals("FAKE", result.getSyncToken());
    }
}