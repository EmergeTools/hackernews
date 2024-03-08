require 'json'
require 'fastlane_core/print_table'
require_relative 'git'

module Fastlane
  module Helper
    module Github
      GITHUB_EVENT_PR = "pull_request".freeze
      GITHUB_EVENT_PUSH = "push".freeze

      def self.repo_id
        remote_url = Helper::Git.remote_url
        return nil unless remote_url

        result = remote_url.match(/[:\/]([^\/]+\/[^\/.]+)\.git$/)
        result.nil? ? nil : result[1]
      end

      def self.repo_owner
        repo_id&.split('/')&.first
      end

      def self.repo_name
        repo_id&.split('/')&.last
      end

      def self.event_name
        ENV['GITHUB_EVENT_NAME']
      end

      def self.is_supported_github_event?
        is_pull_request? || is_push?
      end

      def self.is_pull_request?
        event_name == GITHUB_EVENT_PR
      end

      def self.is_push?
        event_name == GITHUB_EVENT_PUSH
      end

      def self.sha
        if is_push?
          ENV['GITHUB_SHA']
        elsif is_pull_request?
          pull_request_event_data.dig(:pr, :head, :sha)
        end
      end

      def self.base_sha
        if is_pull_request?
          pull_request_event_data.dig(:pr, :base, :sha)
        end
      end

      def self.pr_number
        is_pull_request? ? pull_request_event_data[:number] : nil
      end

      private

      def self.pull_request_event_data
        github_event_path = ENV['GITHUB_EVENT_PATH']
        UI.error!("GITHUB_EVENT_PATH is not set") if github_event_path.nil?

        unless File.exist?(github_event_path)
          UI.error!("File #{github_event_path} doesn't exist")
        end

        file_content = File.read(github_event_path)
        JSON.parse(file_content, symbolize_names: true)
      end
    end
  end
end
