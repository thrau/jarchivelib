CPF = compress.txt
AR  = archive

all: archives compress

## archives
## TODO: separate
archives:
	cd $(AR);                                                         \
	tar czf ../$(AR).tar.gz *;                                        \
	tar cjf ../$(AR).tar.bz2 *;                                       \
	tar cf ../$(AR).tar *;                                            \
	zip -q -r ../$(AR) .;                                             \
	jar cMf ../$(AR).jar .;                                           \
	find . -depth ! -path . -print | cpio --quiet -o > ../$(AR).cpio; \
	7z a -t7z ../$(AR).7z . > /dev/null;

## compress
compress: $(CPF).gz $(CPF).lzma $(CPF).bz2 $(CPF).xz

$(CPF).gz:
	gzip -c $(CPF) > $@

$(CPF).bz2:
	bzip2 -c $(CPF) > $@

$(CPF).lzma:
	lzma -z -c $(CPF) > $@

$(CPF).xz:
	xz -z -c $(CPF) > $@

## clean
clean: clean-compress clean-archives

clean-compress:
	rm -f $(CPF).*

clean-archives:
	rm -f $(AR).*
