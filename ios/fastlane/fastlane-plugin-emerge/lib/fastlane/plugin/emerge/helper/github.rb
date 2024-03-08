require 'json'
require 'fastlane_core/print_table'
require_relative 'git'

module Fastlane
  module Helper
    module Github
      GITHUB_EVENT_PR = "pull_request".freeze
      GITHUB_EVENT_PUSH = "push".freeze

      def self.event_name
        ENV['GITHUB_EVENT_NAME']
      end

      def self.is_supported_github_event?
        UI.message("GitHub event name: #{event_name}")
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
          github_event_data.dig(:pull_request, :head, :sha)
        end
      end

      def self.base_sha
        if is_pull_request?
          github_event_data.dig(:pull_request, :base, :sha)
        end
      end

      def self.pr_number
        is_pull_request? ? github_event_data.dig(:number) : nil
      end

      def self.branch
        is_pull_request? ? github_event_data.dig(:pull_request, :head, :ref) : Git.branch
      end

      def self.repo_owner
        github_event_data.dig(:repository, :owner, :login)
      end

      def self.repo_name
        github_event_data.dig(:repository, :full_name)
      end

      private

      def self.github_event_data
        github_event_path = ENV['GITHUB_EVENT_PATH']
        UI.error!("GITHUB_EVENT_PATH is not set") if github_event_path.nil?

        unless File.exist?(github_event_path)
          UI.error!("File #{github_event_path} doesn't exist")
        end

        file_content = File.read(github_event_path)
        file_json = JSON.parse(file_content, symbolize_names: true)
        UI.message("Parsed GitHub event data: #{file_json.inspect}")
        file_json
      end
    end
  end
end
