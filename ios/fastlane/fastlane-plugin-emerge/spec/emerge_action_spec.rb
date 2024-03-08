describe Fastlane::Actions::EmergeAction do
  describe '#run' do
    it 'prints a message' do
      expect(Fastlane::UI).to receive(:message).with("The emerge plugin is working!")

      Fastlane::Actions::EmergeAction.run([])
    end
  end
end
