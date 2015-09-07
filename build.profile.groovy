// default settings
core {
    log_path         = System.getProperty("user.home") + "/temp/logs"

    upload_save_path = System.getProperty("user.home") + "/temp/tomcat_uploaded"
    upload_temp_path = System.getProperty("user.home") + "/temp/tomcat_upload_temp"

    gcp_auth_data_dir = System.getProperty("user.home") + "/work/gcp-config"
    gcp_client_secret_filename = "client_secret_10838234188-d59ibeotidu52u7cvp2pc4bbdmg5j4l5.apps.googleusercontent.com.json"
    gcp_oauth_app_name = "bldemo"
    gcp_storage_bucket_name = "springmvc-demp-upload-temp"


    gcp_auth_p12_file = "DemoProject-ee78999ca2f1.p12"
    gcp_auth_p12_pwd  = "notasecret"
    gcp_auth_service_account_email = "10838234188-vflpvhq4j009m9r8le0dmiktd1btgrl4@developer.gserviceaccount.com"

    env_name         = "dev"
}

app {

}

// environment specific values, just need set the difference of default
environments {

    wang {

    }

    whd {
        core {
            gcp_auth_data_dir = System.getProperty("user.home") + "/work/gcp"

			gcp_auth_p12_file = "DemoProject-cc7a1b9d38c4.p12"
            gcp_client_secret_filename = "client_secret_10838234188-mdlh7ol37tcgba6nittc3feb2ustgh7t.apps.googleusercontent.com.json"

        }

    }

    test {
        core {
            env_name = "test"
        }
    }

    staging {
        core {
            env_name = "staging"
        }
    }

    production {
        core {
            env_name = "production"
        }
    }

}
