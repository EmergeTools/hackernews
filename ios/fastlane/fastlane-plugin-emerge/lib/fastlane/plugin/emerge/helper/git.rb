require 'fastlane_core/print_table'
require 'open3'

module Fastlane
  module Helper
    module Git
      def self.branch
        shell_command = "git rev-parse --abbrev-ref HEAD"
        UI.command(shell_command)
        stdout, _, status = Open3.capture3(shell_command)
        stdout.strip if status.success?
      end

      def self.sha
        shell_command = "git rev-parse HEAD"
        UI.command(shell_command)
        stdout, _, status = Open3.capture3(shell_command)
        stdout.strip if status.success?
      end

      def self.base_sha
        shell_command = "git merge-base #{remote_head_branch} #{branch}"
        UI.command(shell_command)
        stdout, _, status = Open3.capture3(shell_command)
        return nil if stdout.strip.empty? || !status.success?
        current_sha = sha
        stdout.strip == current_sha ? nil : stdout.strip
      end

      def self.primary_remote
        remote = remote()
        return nil if remote.nil?
        remote.include?("origin") ? "origin" : remote.first
      end

      def self.remote_head_branch(remote = primary_remote)
        return nil if remote.nil?
        shell_command = "git remote show #{remote}"
        UI.command(shell_command)
        stdout, _, status = Open3.capture3(shell_command)
        return nil if stdout.nil? || !status.success?
        stdout
          .split("\n")
          .map(&:strip)
          .find { |line| line.start_with?("HEAD branch: ") }
          &.split(' ')
          &.last
      end

      def self.remote_url(remote = primary_remote)
        return nil if remote.nil?
        shell_command = "git config --get remote.#{remote}.url"
        UI.command(shell_command)
        stdout, _, status = Open3.capture3(shell_command)
        stdout if status.success?
      end

      def self.remote
        shell_command = "git remote"
        UI.command(shell_command)
        stdout, _, status = Open3.capture3(shell_command)
        stdout.split("\n") if status.success?
      end
    end
  end
end
