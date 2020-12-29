function krnCreateFile(name)
{
	return krnFileSystemDriver.createFile(name);
}

function krnReadFile(name, length)
{
	return krnFileSystemDriver.readFile(name, length);
}

function krnWriteFile(name, bytes)
{
	return krnFileSystemDriver.writeFile(name, bytes);
}

function krnDeleteFile(name)
{
	return krnFileSystemDriver.deleteFile(name, length);
}

function krnFormatFileSystem()
{
	return krnFileSystemDriver.format();
}

function krnGetFilesize(name)
{
	return krnFileSystemDriver.getFilesize(name);
}
