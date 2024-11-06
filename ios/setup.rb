require 'dry/cli'
require 'fileutils'
require 'logger'

module EmergeCLI
  module Logger
    class << self
      def configure(level)
        @logger = ::Logger.new(STDOUT)
        @logger.level = level
        @logger.formatter = proc { |_, _, _, msg| "#{msg}\n" }
      end

      def info(message)
        @logger.info(message)
      end

      def error(message)
        @logger.error(message)
      end
    end
  end
end

EmergeCLI::Logger.configure(::Logger::INFO)

module EmergeCLI
  module Commands
    module Integrate
      class Fastlane < Dry::CLI::Command
        desc 'Integrate Emerge into your iOS project via Fastlane'

        argument :path, type: :string, required: false, default: '.', desc: 'Project path (defaults to current directory)'

        def call(path: '.', api_token: nil, **options)
          @project_path = File.expand_path(path)
          Logger.info "Project path: #{@project_path}"

          Logger.info "🔍 Detecting project type..."
          
          # if detector.ios_project?
          Logger.info "📱 iOS project detected!"
          setup_ios
          # else
          #   Logger.error "❌ Error: Could not detect project. Make sure you're in the root directory of an iOS project."
          #   exit 1
          # end
        end

        private

        def setup_ios
          Logger.info "Setting up Emerge Tools for iOS project using Fastlane..."
          
          # Install Fastlane if needed
          if !command_exists?('fastlane')
            Logger.info "Fastlane not found. Would you like to install it? (y/n)"
            response = nil
            while response != 'y' && response != 'n'
              response = STDIN.getc.downcase
              if response != 'y' && response != 'n'
                Logger.info "Please enter 'y' or 'n'"
              end
            end
            if response == 'y'
              Logger.info "Installing Fastlane..."
              system('gem install fastlane')
            else
              Logger.info "Skipping Fastlane installation"
              exit 1
            end
          end

          setup_gemfile
          setup_fastfile
          
          # Install Emerge Fastlane plugin
          Logger.info "Installing Emerge Fastlane plugin..."
          system('fastlane add_plugin emerge')

          print_ios_completion_message
        end

        def setup_gemfile
          gemfile_path = File.join(@project_path, 'Gemfile')
          gemfile_content = <<~RUBY
            source "https://rubygems.org"

            gem "xcpretty"
            gem "fastlane"
            plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
            eval_gemfile(plugins_path) if File.exist?(plugins_path)
          RUBY

          if File.exist?(gemfile_path)
            Logger.info "Updating existing Gemfile..."
            current_content = File.read(gemfile_path)
            current_content << "\ngem 'fastlane'" unless current_content.include?('gem "fastlane"')
            current_content << "\ngem 'xcpretty'" unless current_content.include?('gem "xcpretty"')
            File.write(gemfile_path, current_content)
          else
            Logger.info "Creating Gemfile..."
            File.write(gemfile_path, gemfile_content)
          end

          Logger.info "Installing gems..."
          system('bundle install')
        end

        def setup_fastfile
          fastfile_dir = File.join(@project_path, 'fastlane')
          FileUtils.mkdir_p(fastfile_dir)
          fastfile_path = File.join(fastfile_dir, 'Fastfile')


          if File.exist?(fastfile_path)
            Logger.info "Updating existing Fastfile..."
            update_existing_fastfile(fastfile_path)
          else
            Logger.info "Fastfile not found. Would you like to create it? (y/n)"
            response = nil
            while response != 'y' && response != 'n'
              response = STDIN.getc.downcase
              if response != 'y' && response != 'n'
                Logger.info "Please enter 'y' or 'n'"
              end
            end
            if response == 'y'
              Logger.info "Creating Fastfile..."
              File.write(fastfile_path, generate_fastfile_content)
            else
              Logger.info "Skipping Fastfile creation"
            end
          end
        end

        def generate_fastfile_content
          <<~'RUBY'
            default_platform(:ios)

            platform :ios do
              lane :app_size do
                build_app(scheme: ENV["SCHEME_NAME"], export_method: "development")
                emerge(tag: ENV['EMERGE_BUILD_TYPE'] || "default")
              end

              desc 'Build and upload snapshot build to Emerge Tools'
              lane :build_upload_emerge_snapshot do
                emerge_snapshot(scheme: ENV["SCHEME_NAME"])
              end
            end
          RUBY
        end

        def update_existing_fastfile(fastfile_path)
          current_content = File.read(fastfile_path)

          # Add platform :ios block if not present
          unless current_content.match?(/platform\s+:ios\s+do/)
            current_content += "\nplatform :ios do\nend\n"
          end

          # Add app_size lane if not present
          unless current_content.match?(/^\s*lane\s*:app_size\s*do/)
            app_size_lane = <<~'RUBY'.gsub(/^/, '  ')
              lane :app_size do
                # NOTE: If you already have a lane setup to build your app, then you can that instead of this and call emerge() after it. 
                build_app(scheme: ENV["SCHEME_NAME"], export_method: "development")
                emerge(tag: ENV['EMERGE_BUILD_TYPE'] || "default")
              end
            RUBY
            current_content.sub!(/platform\s+:ios\s+do.*$/) { |match| match + "\n" + app_size_lane }
          end

          # Add snapshots lane if not present
          unless current_content.match?(/^\s*lane\s*:build_upload_emerge_snapshot\s*do/)
            snapshot_lane = <<~'RUBY'.gsub(/^/, '  ')
              desc 'Build and upload snapshot build to Emerge Tools'
              lane :build_upload_emerge_snapshot do
                emerge_snapshot(scheme: ENV["SCHEME_NAME"])
              end
            RUBY
            current_content.sub!(/lane\s+:app_size\s+do.*?end/m) { |match| match + "\n\n" + snapshot_lane }
          end

          # Clean up any multiple blank lines
          current_content.gsub!(/\n{3,}/, "\n\n")
          
          File.write(fastfile_path, current_content)
        end

        def command_exists?(command)
          system("which #{command} > /dev/null 2>&1")
        end

        def print_ios_completion_message
          Logger.info "✅ iOS setup complete! Don't forget to:"
          Logger.info "1. Set your EMERGE_API_TOKEN environment variable (both locally and in your CI/CD pipeline)"
          Logger.info "2. Set your SCHEME_NAME environment variable (optional, defaults to the first scheme in your project)"
          Logger.info "3. Run 'fastlane app_size' to analyze your app"
          Logger.info "4. Run 'fastlane build_upload_emerge_snapshot' to analyze your snapshots"
        end

        def print_android_completion_message
          Logger.info "✅ Android setup complete! Don't forget to:"
          Logger.info "1. Set your EMERGE_API_TOKEN environment variable (both locally and in your CI/CD pipeline)"
          Logger.info "2. Run './gradlew emergeSizeAnalysisPreflightRelease' to verify setup"
          Logger.info "3. Run './gradlew emergeUploadReleaseAab' to analyze your app"
        end
      end
    end
  end
end

# Invoke the integrate command
Dry::CLI.new(EmergeCLI::Commands::Integrate::Fastlane).call
