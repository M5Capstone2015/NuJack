package NuJack;

public class nByte
{
    private int _value = 0;
    private int _count = 0;

    public float GetValue() 
    {
	    return  ((float) _value) / 10;
    }

    public float intVal()
    {
	    return _value;
    }

    public boolean push(int bit)
    {
	    if (bit != 0 && bit != 1)
	    {
		    // log error
		    return false;
	    }

	    if (_count > 0) // We don't need to shift to set first bit
	    	_value <<= 1;

	    _value += bit;
	    _count++;
	    return true;
    }

    public boolean IsByteComplete()
    {
	    if (_count >= 9)
		    return true;
	    else
		    return false;
    }

    public void Reset()
    {
	    _value = 0;
	    _count = 0;
    }
}
