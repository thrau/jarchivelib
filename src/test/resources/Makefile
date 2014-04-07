COMPRESS_FILE = compress.txt
ARCHIVE       = archive

all: archives compress

archives:
	cd $(ARCHIVE);                                                         \
	tar czf ../$(ARCHIVE).tar.gz *;                                        \
	tar cjf ../$(ARCHIVE).tar.bz2 *;                                       \
	tar cf ../$(ARCHIVE).tar *;                                            \
	zip -q -r ../$(ARCHIVE) .;                                             \
	jar cMf ../$(ARCHIVE).jar .;                                           \
	find . -depth ! -path . -print | cpio --quiet -o > ../$(ARCHIVE).cpio; \
	7z a -t7z ../$(ARCHIVE).7z . > /dev/null;

compress:
	gzip -c $(COMPRESS_FILE) > $(COMPRESS_FILE).gz
	bzip2 -c $(COMPRESS_FILE) > $(COMPRESS_FILE).bz2

clean: clean-compress clean-archives

clean-compress:
	rm -f $(COMPRESS_FILE).*

clean-archives:
	rm -f $(ARCHIVE).*