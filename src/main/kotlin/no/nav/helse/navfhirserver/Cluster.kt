package no.nav.helse.navfhirserver

import no.nav.helse.navfhirserver.Constants.DEV
import no.nav.helse.navfhirserver.Constants.DEV_GCP
import no.nav.helse.navfhirserver.Constants.GCP
import no.nav.helse.navfhirserver.Constants.LOCAL
import no.nav.helse.navfhirserver.Constants.NAIS_CLUSTER_NAME
import no.nav.helse.navfhirserver.Constants.NAIS_IMAGE_NAME
import no.nav.helse.navfhirserver.Constants.NAIS_NAMESPACE_NAME
import no.nav.helse.navfhirserver.Constants.PROD
import no.nav.helse.navfhirserver.Constants.PROD_GCP
import no.nav.helse.navfhirserver.Constants.TEST
import java.lang.System.getenv
import java.lang.System.setProperty

internal object Constants {
    internal const val LOCAL = "local"
    internal const val GCP = "gcp"
    internal const val TEST = "test"
    internal const val DEV = "dev"
    internal const val PROD = "prod"
    internal  const val DEV_GCP = "${DEV}-${GCP}"
    internal const val PROD_GCP = "${PROD}-${GCP}"
    internal const val NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME"
    internal const val NAIS_NAMESPACE_NAME = "NAIS_NAMESPACE"
    internal  const val NAIS_IMAGE_NAME = "NAIS_APP_IMAGE"
}

internal enum class Cluster(private val clusterName: String) {
    TEST_CLUSTER(TEST),
    LOCAL_CLUSTER(LOCAL),
    DEV_GCP_CLUSTER(DEV_GCP),
    PROD_GCP_CLUSTER(PROD_GCP);

    companion object {

        private val cluster = getenv(NAIS_CLUSTER_NAME) ?: LOCAL
        val current = Cluster.entries.firstOrNull { it.clusterName == cluster } ?: LOCAL_CLUSTER
        val image = getenv(NAIS_IMAGE_NAME) ?: "n/a"
        val namespace = getenv(NAIS_NAMESPACE_NAME) ?: LOCAL
        val isProd = current == PROD_GCP_CLUSTER
        val isDev = current == DEV_GCP_CLUSTER
        val isNais = current in listOf(DEV_GCP_CLUSTER, PROD_GCP_CLUSTER)
        val profiler =
            when (current) {
                TEST_CLUSTER, LOCAL_CLUSTER ->
                    arrayOf(current.clusterName).also {
                        setProperty(NAIS_CLUSTER_NAME, current.clusterName)
                    }
                DEV_GCP_CLUSTER -> arrayOf(DEV, DEV_GCP, GCP)
                PROD_GCP_CLUSTER -> arrayOf(PROD, PROD_GCP, GCP)
            }
    }
}