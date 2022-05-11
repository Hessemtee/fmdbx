package com.fmdbx.fm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fmdbx.fm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Version.class);
        Version version1 = new Version();
        version1.setId(1L);
        Version version2 = new Version();
        version2.setId(version1.getId());
        assertThat(version1).isEqualTo(version2);
        version2.setId(2L);
        assertThat(version1).isNotEqualTo(version2);
        version1.setId(null);
        assertThat(version1).isNotEqualTo(version2);
    }
}
