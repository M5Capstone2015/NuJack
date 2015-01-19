package NuJack;



public class AmpReader {

	
	///////////////////////////////////////////////
	// Constants
	///////////////////////////////////////////////	
	
	// Most Android devices support 'CD-quality' sampling frequencies.
	final private int _sampleFrequency = 44100;
	
	// HiJack is powered at 21kHz
	private int _powerFrequency = 21000;
	
	// IO is FSK-modulated at either 613 or 1226 Hz (0 / 1)
	final private int _ioBaseFrequency = 613;

	///////////////////////////////////////////////
	// Main interfaces
	///////////////////////////////////////////////	
	
	private IncomingSink _sink = null;
    
    	// For Audio Input
    	IAudioRecord _audioRecord;
    	Thread _inputThread;
    
	private short[] _recBuffer;
	
	private boolean _isInitialized = false;
	private boolean _isRunning = false;
	private boolean _stop = false;
	
	//----- NEW -----//
	private final int ThresholdAmplitude = 3000;
	private final int BaudRate = 120; // Arbitrary ATM
	private int _count = 0; // Where we are in this Bit 
	private int _zeroCount = 0; 
	private float _mean = 0;
	private float _sum = 0;
	private State _state = State.IDLE;

	private enum State {IDLE, ACTIVE};
	//---------------//


    	Runnable _inputProcessor = new Runnable() {
    		public void run() 
		{
    			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    			while (!_stop) 
			{
    				int shortsRead = _audioRecord.read(_recBuffer, 0, _recBuffer.length);
    				processInputBuffer(shortsRead);
    			}
    		}
    	};

    	private void processInputBuffer(int shortsRead)
    	{

	    	for (int i=0; i < shortsRead; i++)
		{
			_count++; // Increment the count corresponding to the current mean
			_sum += _recBuffer[i]; // Calc new sum
			_mean = _sum / _count; // Calc new mean

			if (_count < BaudRate) // This "clock-pulse" is done reading.
			{
				// if IDLE
				boolean isOne = IsOne();
				if (_state == State.IDLE && isOne)
					_state = State.ACTIVE;
				else
				{
					boolean res = false;
					//
					// Three resp. from framer:
					// 1) Continue -> keep going
					// 2) Bad header or footer -> Do we want to go to IDLE here?
					// 				there will be more Data coming in.
					// 				Maybe just continue. If framer is like
					// 				a stack this shouldn't throw off position.
					// 				So more or less 2 & 3 are the same.
					// 3) Done -> Set to IDLE
					if (isOne)
					{
						// Push ONE
						// res = _sink.handleNextBit(Bit.One);
						_zeroCount = 0;
					}
					else
					{
						// Push Zero
						// res = _sink.handleNextBit(Bit.Zero);
						_zeroCount++;
					}
					if (res || _zeroCount > 8) // Reset to IDLE
						_state = State.IDLE;
				}
				 _count = 0; // Reset everything
				 _mean = 0;
				 _sum = 0;
			}
		}
    	}

	private boolean IsOne()
	{
		//return _mean > ThresholdAmplitude + ThreshRange && _mean < ThresholdAmplitude - ThreshRange;
		return _mean > ThresholdAmplitude;
	}
	
	public int getPowerFrequency() {
		return _powerFrequency;
	}
	
	public void setPowerFrequency(int powerFrequency) {
		_powerFrequency = powerFrequency;
	}
	
	
	public void registerIncomingSink(IncomingSink sink) {
		if (_isRunning) {
			throw new UnsupportedOperationException("AudioIO must be stopped to set a new sink.");
		}		
		_sink = sink;
	}

	public void initialize() {
		// Create buffers to hold what a high and low 
		// frequency waveform looks like
		int bufferSize = getBufferSize();
		_isInitialized = true;
	}
	
	public void startAudioIO() {

		if (!_isInitialized) {
			initialize();
		}
		
		if (_isRunning) {
			return;
		}
		
		_stop = false;
		
		attachAudioResources();
		
		_audioRecord.startRecording();
		
		_inputThread = new Thread(_inputProcessor);
		//_inputThread.start(); // disabled for debuging....
		
		// DEBUG 
		fakeAudioRead(); // Commentiig this out for now just to test this class.
		// DEBUG

	}
	
	//
	// DEBUG 
	//
	private void fakeAudioRead()
	{
		_recBuffer = _audioRecord.read();
		System.out.println("data size: " + _recBuffer.length);
		processInputBuffer(_recBuffer.length);
	}
	


	//
	// DEBUG
	//
	public void printSink()
	{
		FakeSink fs = (FakeSink) _sink;
		fs.Print();
	}
	
	public void stopAudioIO() {
		_stop = true;	
		
		try {
			//_outputThread.join();
			_inputThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		releaseAudioResources();
		
		_isRunning = false;
	}
	
	///////////////////////////////////////////////
	// Support functions
	///////////////////////////////////////////////
	
	private void attachAudioResources() {
		int bufferSize = getBufferSize();
		

		// COUMMENTED OUT FOR DEBUGGING
		/*
		_audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
					     _sampleFrequency, 
					     AudioFormat.CHANNEL_OUT_STEREO,
                			     AudioFormat.ENCODING_PCM_16BIT, 
					     44100,
                			     AudioTrack.MODE_STREAM);     
		*/

		int recBufferSize = 5;
		//int recBufferSize = 
				//AudioRecord.getMinBufferSize(_sampleFrequency, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		/*
		_audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
		        _sampleFrequency, AudioFormat.CHANNEL_IN_MONO,
		        AudioFormat.ENCODING_PCM_16BIT, recBufferSize);
			*/
		_audioRecord = new FakeAudioRecord();
		
		_recBuffer = new short[recBufferSize * 10];
	}
	
	private void releaseAudioResources() {
		//_audioTrack.release();
		_audioRecord.release();
		
		//_audioTrack = null;
		_audioRecord = null;		
		
		_recBuffer = null;
	}
	
    private int getBufferSize() {
    	return _sampleFrequency / _ioBaseFrequency / 2;
    }
}
