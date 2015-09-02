// default settings
core {
	log_path         = System.getProperty("user.home") + "/temp/logs"

	upload_save_path = System.getProperty("user.home") + "/temp/tomcat_uploaded"
	upload_temp_path = System.getProperty("user.home") + "/temp/tomcat_upload_temp"

	gcp_auth_data_dir = System.getProperty("user.home") + "/work/gcp-config"
	gcp_client_secret_filename = "DemoProject-3f5f2f71cb8c.json"
	gcp_project_name = "DemoProject"
	gcp_storage_bucket_name = "springmvc-demp-upload-temp"

	env_name         = "dev"
}

app {

}

// environment specific values, just need set the difference of default
environments {

	wang {

	}

	whd {

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