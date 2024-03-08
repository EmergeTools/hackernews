require 'fastlane_core/ui/ui'
require 'faraday'

module Fastlane
  UI = FastlaneCore::UI unless Fastlane.const_defined?("UI")

  class GitResult
    attr_accessor :sha, :base_sha, :branch, :pr_number, :repo_name

    def initialize(sha:, base_sha:, branch:, pr_number: nil, repo_name: nil)
      @pr_number = pr_number
      @sha = sha
      @base_sha = base_sha
      @branch = branch
      @repo_name = repo_name
    end
  end

  module Helper
    class EmergeHelper
      def self.perform_upload(upload_url, upload_id, file_path)
        UI.message("Starting upload")
        response = Faraday.put(upload_url) do |req|
          req.headers['Content-Type'] = 'application/zip'
          req.headers['Content-Length'] = File.size(file_path).to_s
          req.body = Faraday::UploadIO.new(file_path, 'application/zip')
        end
        case response.status
        when 200
          UI.success("🎉 Your app is processing, you can find the results at https://emergetools.com/build/#{upload_id}")
          return upload_id
        else
          UI.error("Upload failed")
        end
        return nil
      end

      def self.make_git_params
        git_result = if Helper::Github.is_supported_github_event?
                       UI.message("Fetching Git info from Github event")
                       GitResult.new(
                         sha: Helper::Github.sha,
                         base_sha: Helper::Github.base_sha,
                         branch: Helper::Github.branch,
                         pr_number: Helper::Github.pr_number,
                         repo_name: Helper::Github.repo_name
                       )
                     else
                       UI.message("Fetching Git info from system Git")
                       GitResult.new(
                         sha: Helper::Git.sha,
                         base_sha: Helper::Git.base_sha,
                         branch: Helper::Git.branch
                       )
                     end
        UI.message("Got git result #{git_result.inspect}")
        git_result
      end
    end
  end
end
