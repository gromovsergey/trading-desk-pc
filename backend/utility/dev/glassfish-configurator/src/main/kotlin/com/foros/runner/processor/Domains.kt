package com.foros.runner.processor

import com.foros.runner.domain.Configuration
import com.foros.runner.domain.configuration

public object Domains {

    public val config:Configuration = configuration {

        (1..20).forEach {
            domain("UI_DEV_${it}") {
                oracle("oradev.ocslab.com", "addbtc.ocslab.com", "UI_DEV_${it}")
                pg("stat-dev0.ocslab.com", "ui_dev_${it}")
                olap(schema = "ForosUi moscow-dev-ui-ui_dev_${it}")
            }
        }

        domain("NB_AUTO") {
            oracle("ora-nb.ocslab.com", "addbnba.ocslab.com", "NB_COPY10")
            pg("stat-nbouiat.ocslab.com", "nb_trunk_auto")
            olap(schema = "ForosUi moscow-nb-oui-at")
        }

        domain("NB_PERF") {
            oracle("oraperf.ocslab.com", "addbpt.ocslab.com", "NIGHTLY_PERF", port=1721)
            pg("stat-nbperf.ocslab.com", "nb_trunk_perf", "oix")
            olap(schema = "ForosUi moscow-nb-oui-perf")
        }

    }

}