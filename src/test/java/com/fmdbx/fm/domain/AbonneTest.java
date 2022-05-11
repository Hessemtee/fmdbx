package com.fmdbx.fm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fmdbx.fm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AbonneTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Abonne.class);
        Abonne abonne1 = new Abonne();
        abonne1.setId(1L);
        Abonne abonne2 = new Abonne();
        abonne2.setId(abonne1.getId());
        assertThat(abonne1).isEqualTo(abonne2);
        abonne2.setId(2L);
        assertThat(abonne1).isNotEqualTo(abonne2);
        abonne1.setId(null);
        assertThat(abonne1).isNotEqualTo(abonne2);
    }
}
