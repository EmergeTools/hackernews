require 'fastlane/action'
require 'tmpdir'
require 'tempfile'
require 'fileutils'

module Fastlane
  module Actions
    class EmergeOrderFileAction < Action
      def self.run(params)
        puts "https://order-files-prod.emergetools.com/#{params[:app_id]}/#{params[:order_file_version]}"
        resp = Faraday.get("https://order-files-prod.emergetools.com/#{params[:app_id]}/#{params[:order_file_version]}", nil, {'X-API-Token' => params[:api_token]})
        case resp.status
        when 200
          Tempfile.create do |f|
            f.write(resp.body)
            decompressed = IO.popen(['gunzip', '-c', f.path]).read
            IO.write(params[:output_path], decompressed)
          end
          return 200
        when 401
          UI.error("Unauthorized")
        when 403
          UI.message("No order file found, this is expected for the first build of an app_id/version.")
          # The API will return a 403 when no order file is found, but we change that to a 200 for the
          # fastlane plugin because this is an expected state for a CI integraion.
          return 200
        else
          UI.error("Failed to download order file code: #{resp.status}")
        end
        resp.status
      end

      def self.description
        "Fastlane plugin to download order files"
      end

      def self.authors
        ["Emerge Tools"]
      end

      def self.return_value
        # If your method provides a return value, you can describe here what it does
      end

      def self.details
        # Optional:
        ""
      end

      def self.available_options
        [
          FastlaneCore::ConfigItem.new(key: :api_token,
                                  env_name: "EMERGE_API_TOKEN",
                               description: "An API token for Emerge",
                                  optional: false,
                                      type: String),
          FastlaneCore::ConfigItem.new(key: :app_id,
                               description: "Id of the app being built with the order file",
                                  optional: false,
                                      type: String),
          FastlaneCore::ConfigItem.new(key: :output_path,
                                   description: "Path to the order file",
                                      optional: false,
                                          type: String),
          FastlaneCore::ConfigItem.new(key: :order_file_version,
                               description: "Version of the order file to download",
                                  optional: false,
                                      type: String),
        ]
      end

      def self.is_supported?(platform)
        platform == :ios
      end
    end
  end
end
