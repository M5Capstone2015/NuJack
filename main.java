//
// Capstone testing HiJack in Console-App
//

import NuJack.*;

class main
{  
    public static void main(String args[])
    {
		//FakeAudioRecord fru = new FakeAudioRecord();
		//fru.loadFile();
		//fru.Print();

		//FakeSink fs = new FakeSink();

		//AudioReceiver aru = new AudioReceiver(fru);
		//aru.registerIncomingSink(fs);
		//aru.startAudioIO();

		/*
		SerialDecoder decoder = new SerialDecoder();
		decoder.start();
		decoder.print();
		*/	
		
		/*
		Decoder dec = new Decoder();
		dec.start();
		dec.print();
		*/
		
		FakeHijackData fhd = new FakeHijackData();
		fhd.Print();
		
		NewDecoder deco = new NewDecoder();
		for (Integer i : fhd.GetData())
		{
			deco.handleNextBit(i, true);
		}
		
					System.out.println("Hit Count: " + deco._hitCount);

	}
}	