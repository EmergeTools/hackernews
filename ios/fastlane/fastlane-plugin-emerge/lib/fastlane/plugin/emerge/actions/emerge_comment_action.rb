require 'fastlane/action'
require 'fastlane_core/print_table'
require_relative '../helper/emerge_helper'

module Fastlane
  module Actions
    class EmergeComment < Action
      def self.run(params)
        url = 'https://api.emergetools.com/getComment'
        api_token = params[:api_token]
        gitlab_url = params[:gitlab_url]
        project_id = params[:gitlab_project_id]
        pr_number = params[:pr_number]
        gitlab_access_token = params[:gitlab_access_token]

        request_params = {
          buildId: params[:build_id],
          baseBuildId: params[:base_build_id]
        }
        resp = Faraday.get(url, request_params, 'x-api-token' => api_token)
        case resp.status
        when 200
          UI.message("Received comment from Emerge")
          baseURL = gitlab_url ? gitlab_url : "https://gitlab.com"
          url = "#{baseURL}/api/v4/projects/#{project_id}/merge_requests/#{pr_number}/notes"
          gitlab_response = Faraday.post(url, { "body" => resp.body }, 'Authorization' => "Bearer #{gitlab_access_token}")
          case gitlab_response.status
          when 200...299
            UI.message("Successfully posted comment")
          else
            UI.error("Received error #{gitlab_response.status} from Gitlab")
          end
        else
          UI.error("Received error #{resp.status} from Emerge")
        end
      end

      def self.description
        "Post an Emerge PR comment, currently supports Gitlab only."
      end

      def self.authors
        ["Emerge Tools"]
      end

      def self.available_options
        [
          FastlaneCore::ConfigItem.new(key: :api_token,
                                       env_name: "EMERGE_API_TOKEN",
                                       description: "An API token for Emerge",
                                       optional: false,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :gitlab_acces_token,
                                       env_name: "GITLAB_ACCESS_TOKEN",
                                       description: "An access token for Gitlab",
                                       optional: false,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :pr_number,
                                       description: "The PR number that triggered this upload",
                                       optional: false,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :build_id,
                                       description: "A string to identify this build",
                                       optional: false,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :base_build_id,
                                       description: "Id of the build to compare with this upload",
                                       optional: false,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :gitlab_url,
                                       description: "URL of the self hosted gitlab instance",
                                       optional: true,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :gitlab_project_id,
                                       description: "Id of the gitlab project this upload was triggered from",
                                       optional: false,
                                       type: Integer)
        ]
      end

      def self.is_supported?(platform)
        platform == :ios
      end
    end
  end
end
