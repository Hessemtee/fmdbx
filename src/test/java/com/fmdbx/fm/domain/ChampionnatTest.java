package com.fmdbx.fm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fmdbx.fm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChampionnatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Championnat.class);
        Championnat championnat1 = new Championnat();
        championnat1.setId(1L);
        Championnat championnat2 = new Championnat();
        championnat2.setId(championnat1.getId());
        assertThat(championnat1).isEqualTo(championnat2);
        championnat2.setId(2L);
        assertThat(championnat1).isNotEqualTo(championnat2);
        championnat1.setId(null);
        assertThat(championnat1).isNotEqualTo(championnat2);
    }
}
